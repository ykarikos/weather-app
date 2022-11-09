(ns weather-app.api
  (:require [clj-http.client :as client]))

(def forecast-url "https://api.open-meteo.com/v1/forecast?hourly=temperature_2m&")
(def geocoding-url "https://geocoding-api.open-meteo.com/v1/search?name=")

(defn- api-get [url]
  (:body (client/get url {:as :json})))

(defn- get-location [city]
  (-> (api-get (str geocoding-url city))
      :results
      first))

(defn- get-avg-temperature-forecast [{:keys [:latitude :longitude]}]
  (let [response (api-get (str forecast-url
                               "latitude=" latitude
                               "&longitude=" longitude))
        times (some-> response
                      :hourly
                      :time)
        temperatures (some-> response
                             :hourly
                             :temperature_2m)]
    {:avg-temperature (/ (reduce + temperatures) (count temperatures))
     :start-date (first times)
     :end-date (last times)}))

(defn forecast-handler [{:keys [path-params]}]
  (let [city (:city path-params)]
    {:status 200
     :body {:avg-temperature-forecast (get-avg-temperature-forecast (get-location city))
            :city city}}))

