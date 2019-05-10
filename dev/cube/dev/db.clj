(ns cube.dev.db
  (:require [crux.api :as crux]
            [clojure.set :refer [rename-keys]])
  (:import [crux.api ICruxAPI]))

(def ^crux.api.ICruxAPI system
  (crux/start-standalone-system {:kv-backend "crux.kv.memdb.MemKv"
                                 :db-dir "data/db-dir-1"}))

(comment
  (crux/submit-tx
   system
   [[:crux.tx/put :dbpedia.resource/Pablo-Picasso ; id for Kafka
     {:crux.db/id :dbpedia.resource/Pablo-Picasso ; id for Crux
      :name "Pablo"
      :last-name "Picasso"}
     #inst "2018-05-18T09:20:27.966-00:00"]])

  (crux/q (crux/db system)
          '{:find [e]
            :where [[e :name "Pablo"]]}))


(defn transact [conn tx-data]
  (let [inst (java.util.Date.)
        tx-data (into []
                      (map (fn [{:keys [:db/id] :as tx}]
                             [:crux.tx/put id
                              (-> tx (rename-keys {:db/id :crux.db/id}))
                              inst]))
                      tx-data)]
    (crux/submit-tx conn tx-data)))

(comment
  (transact system [{:db/id :foobar
                     :mtg.card/name "Serra Angel"}
                    {:db/id :foobar2
                     :mtg.card/name "Duress"}])

  (crux/q (crux/db system)
          '{:find [e]
            :where [[e :mtg.card/name _]]}))
