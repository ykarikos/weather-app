(ns weather-app.prod
  (:require [weather-app.core :as core]))

;;ignore println statements in prod
(set! *print-fn* (fn [& _]))

(core/init!)
