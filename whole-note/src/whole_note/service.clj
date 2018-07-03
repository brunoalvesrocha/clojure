(ns whole-note.service
  (:require [io.pedestal.http :as http]
            [io.pedestal.http.route :as route]
            [io.pedestal.http.body-params :as body-params]
            [ring.util.response :as ring-resp]))

(defn about-page
  [request]
  (ring-resp/response (format "Clojure %s - served from %s"
                              (clojure-version)
                              (route/url-for ::about-page))))

(defn home-page
  [request]
  (ring-resp/response "Hello World!"))

(defn mock-project-collection
  {
   :sleeping-cat
   {
    :name      "Sleeping Cat Project"
    :framework "Pedestal"
    :language  "Clojure"
    :repo      "https://gitlab.com/srehorn/sleepingcat"
    }
   :stinky-dog
   {
    :name      "Stinky Dog Experiment"
    :framework "Grails"
    :language  "Groovy"
    :repo      "https://gitlab.com/srehorn/stinkydog"
    }
   }
  )

(defn get-projects
  [request]
  (http/json-response mock-project-collection))

(def common-interceptors [(body-params/body-params) http/html-body])

;; Tabular routes
(def routes #{["/" :get (conj common-interceptors `home-page)]
              ["/projects" {:get get-projects}]
              ["/about" :get (conj common-interceptors `about-page)]})

(def service {:env                     :prod
              ::http/routes            routes

              ::http/resource-path     "/public"

              ::http/type              :jetty
              ::http/port              (Integer. (or (System/getenv "PORT") 2057))
              ::http/container-options {:h2c? true
                                        :h2?  false
                                        :ssl? false}})

