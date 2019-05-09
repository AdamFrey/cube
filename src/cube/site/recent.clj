(ns cube.site.recent
  (:require [hiccup.core :as hiccup]
            [cube.site.common :as common]))

(defn html []
  (hiccup/html
   (common/page
    [:div.container
     [:h1.title "Recent Tracks"]])))
