(ns cube.site.home
  (:require [hiccup.core :as hiccup]
            [cube.site.common :as common]))

(defn html []
  (hiccup/html
   (common/page
    [:section.hero.is-info.is-fullheight
     [:div.hero-head
      [:nav.navbar
       [:div.container
        [:div.navbar-brand
         [:div.navbar-item
          [:h1.title "My Cube"]]]]]]

     [:div.hero-body
      [:div.container
       [:a.button.is-primary.is-large {:href "/login"} "Login"]]]

     [:div.hero-foot
      [:nav.tabs
       [:div.container
        [:ul
         [:li [:a {:href "/faq"} "FAQ"]]]]]]])))
