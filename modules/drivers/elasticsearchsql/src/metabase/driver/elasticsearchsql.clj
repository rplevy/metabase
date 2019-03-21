(ns metabase.driver.elasticsearchsql
  (:require [clojure.string :as str]
            [honeysql
             [core :as hsql]
             [format :as hformat]]
            [metabase
             [config :as config]
             [driver :as driver]]
            [metabase.driver
             [common :as driver.common]
             [sql :as sql]]
            [metabase.driver.sql-jdbc
             [connection :as sql-jdbc.conn]
             [sync :as sql-jdbc.sync]]))

(driver/register! :elasticsearchsql, :parent :sql-jdbc)

(defmethod sql-jdbc.conn/connection-details->spec :elasticsearchsql
  [_ details]
  (merge {:subprotocol "es"} details))
