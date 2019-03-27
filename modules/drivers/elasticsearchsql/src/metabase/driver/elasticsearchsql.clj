(ns metabase.driver.elasticsearchsql
  "interact with Elasticsearch via its SQL access REST API
  https://www.elastic.co/guide/en/elasticsearch/reference/current/sql-rest.html"
  (:require [clojure.string :as str]
            [cheshire.core :as json]
            [clj-http.client :as http]
            [metabase
             [config :as config]
             [driver :as driver]]
            [metabase.driver
             [common :as driver.common]
             [sql :as sql]]))

(def es-base-url "http://localhost:9200") ;; todo: get from config

(driver/register! :elasticsearchsql)

(defn kebob-kw [s]
  (keyword (str/replace s "_" "-")))

(defn prepare-columns [columns]
  (map (comp kebob-kw :name) columns))

(defn prepare-sql-results [{:keys [columns rows] :as results}]
  (map (fn [row]
         (into {} (map vector (prepare-columns columns) row)))
       rows))

(defn query-es* [sql]
  (->
   (http/post (str es-base-url "/_xpack/sql?format=json")
              {:headers {"Content-Type" "application/json"}
               :accept :json
               :body (json/generate-string {:query sql})})
   :body
   (json/parse-string true)))

(defn get-es-indices []
  (-> (http/get (str es-base-url "/_cat/indices?format=json"))
      :body
      (json/parse-string true)))

(defn get-table-fields [table-name]
  (let [{:keys [columns] :as results}
        (query-es* (format "select * from %s" table-name))]
    (prepare-columns columns)))

(defn query-es [sql]
  (prepare-sql-results (query-es* sql)))

;; https://www.elastic.co/guide/en/kibana/current/tutorial-load-dataset.html
;; curl -H 'Content-Type: application/x-ndjson' -XPOST 'localhost:9200/shakespeare/doc/_bulk?pretty' --data-binary @shakespeare_6.0.json
;; (query-es "select * from shakespeare")

(defmethod driver/supports? [:elasticsearchsql :basic-aggregations] [_ _]
  true)

(defmethod driver/can-connect? :elasticsearchsql [_ _]
  true)

(defn prepare-table-name [table-name]
  {:name table-name
   :schema "PUBLIC"
   :description nil})

(defn prepare-field-name [field-name]
  {:name field-name
   :database-type "varchar"
   :base-type :type/Text})

(defmethod driver/describe-database :elasticsearchsql [_ _]
  {:tables (set (map
                 (comp prepare-table-name :index)
                 (get-es-indices)))})

#_(driver/describe-database :elasticsearchsql nil)

(defmethod driver/describe-table :elasticsearchsql [_ _ {table-name :name}]
  {:name   (str/upper-case table-name)
   :schema "PUBLIC"
   :fields (set (map prepare-field-name
                     (get-table-fields table-name)))})

#_(driver/describe-table :elasticsearchsql nil {:name "shakespeare"})

(defmethod driver/mbql->native :elasticsearchsql
  [_ mbql-query]
  (println "mbql-query:" mbql-query))

(defmethod driver/execute-query :elasticsearchsql
  [_ native-query]
  (println "native-query:" native-query))
