(ns cube.service
  (:require [immuconf.config :as config]
            [io.pedestal.http :as http]
            [io.pedestal.http.body-params :as body-params]
            [io.pedestal.http.content-negotiation :as content-neg]
            [io.pedestal.http.ring-middlewares :as middleware]
            [io.pedestal.http.route :as route]
            [io.pedestal.interceptor :as interceptor]
            [io.pedestal.log :as log]
            [cube.site.home :as home]
            [cube.site.recent :as site.recent]
            [ring.middleware.session.cookie :as cookie]
            [ring.util.response :as ring-resp]))

(defn config [& path]
  (let [cfg (config/load "./config.edn" "./secrets.edn")]
    (apply config/get cfg path)))

(def supported-types ["text/html"])

(def content-negotiation-interceptor (content-neg/negotiate-content supported-types))

(defn home
  [request]
  {:status 200
   :body (home/html)
   :headers {"Content-Type" "text/html"}})

(defn login
  [request]
  (ring-resp/redirect "login"))


(defn recent [req]
  (prn "recent session" (select-keys req [:session]))
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body (site.recent/html)})


(def common-interceptors
  [(body-params/body-params)
   http/json-body
   content-negotiation-interceptor
   (middleware/session {:store (cookie/cookie-store)})])

(def routes
  #{["/" :get (conj common-interceptors `home)]
    ["/login" :get (conj common-interceptors `login)]
    ["/recent" :get (conj common-interceptors `recent)]
    ;; ["/pet/:id" :get (into app-interceptors [pet-interceptor `get-pet])]
    })

;; See http/default-interceptors for additional options you can configure
(def service {;; You can bring your own non-default interceptors. Make
              ;; sure you include routing and set it up right for
              ;; dev-mode. If you do, many other keys for configuring
              ;; default interceptors will be ignored.
              ;; ::http/interceptors []
              ::http/routes routes
              ::http/resource-path "/public"
              ;;::http/chain-provider provider/ion-provider
              })
