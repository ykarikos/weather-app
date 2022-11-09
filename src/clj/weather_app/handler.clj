(ns weather-app.handler
  (:require
   [reitit.ring :as reitit-ring]
   [weather-app.middleware :refer [middleware]]
   [weather-app.api :as api]
   [reitit.ring.middleware.muuntaja :as muuntaja]
   [reitit.ring.middleware.parameters :as parameters]
   [muuntaja.core :as m]
   [hiccup.page :refer [include-js include-css html5]]
   [config.core :refer [env]]))

(def mount-target
  [:div#app
   [:h2 "Welcome to weather-app"]
   [:p "please wait while Figwheel/shadow-cljs is waking up ..."]
   [:p "(Check the js console for hints if nothing exciting happens.)"]])

(defn head []
  [:head
   [:meta {:charset "utf-8"}]
   [:meta {:name "viewport"
           :content "width=device-width, initial-scale=1"}]
   (include-css (if (env :dev) "/css/site.css" "/css/site.min.css"))])

(defn loading-page []
  (html5
   (head)
   [:body {:class "body-container"}
    mount-target
    (include-js "/js/app.js")]))


(defn index-handler
  [_request]
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body (loading-page)})

(def ring-opts
  {:data
   {:muuntaja m/instance
    :middleware [parameters/parameters-middleware
                 muuntaja/format-middleware]}})

(defn api-handler [_]
  {:status 200
   :body {:api-data [1 2 3]}})

(def app
  (reitit-ring/ring-handler
   (reitit-ring/router
    [["/" {:get {:handler index-handler}}]
     ["/items"
      ["" {:get {:handler index-handler}}]
      ["/:item-id" {:get {:handler index-handler
                          :parameters {:path {:item-id int?}}}}]]
     ["/about" {:get {:handler index-handler}}]
     ["/api/test" {:get {:handler api-handler}}]
     ["/api/forecast/:city" {:get {:handler api/forecast-handler}}]]
    ring-opts)
   (reitit-ring/routes
    (reitit-ring/create-resource-handler {:path "/" :root "/public"})
    (reitit-ring/create-default-handler))
   {:middleware middleware}))
