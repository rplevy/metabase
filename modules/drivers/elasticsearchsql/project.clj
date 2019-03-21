(defproject metabase/elasticsearchsql-driver "0.1.0-SNAPSHOT"
  :min-lein-version "2.5.0"

  :dependencies
  [#_[org.elasticsearch.plugin/x-pack-sql-jdbc "8.0.0-alpha1"] ; official driver is not free
   [com.github.wyukawa.elasticsearch.unofficial.jdbc.driver/elasticsearch-jdbc-driver "0.0.9"]]

  :profiles
  {:provided
   {:dependencies [[metabase-core "1.0.0-SNAPSHOT"]]}

   :uberjar
   {:auto-clean    true
    :aot           :all
    :javac-options ["-target" "1.8", "-source" "1.8"]
    :target-path   "target/%s"
    :uberjar-name  "elasticsearchsql.metabase-driver.jar"}})
