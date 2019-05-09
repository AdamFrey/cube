(ns cube.site.common
  (:require [hiccup.core :as hiccup]))

(defn page [content]
  [:html
   [:head
    [:meta {:charset "utf-8"}]
    [:meta {:name "viewport" :content "width=device-width, initial-scale=1"}]
    [:link {:rel "stylesheet" :href "https://cdnjs.cloudflare.com/ajax/libs/bulma/0.7.4/css/bulma.min.css"}]
    [:title "My Cube Archive"]]
   [:body
    content]])
