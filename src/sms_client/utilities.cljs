(ns sms-client.utilities
  (:require [cljs-time.coerce :as time-c]
            [cognitect.transit :as transit]))

(set! js/window.React (js/require "react-native"))

(def platform-os (.-OS (.-Platform js/React)))
(def ios? (= platform-os "ios"))
(def android? (= platform-os "android"))

(def irish-mobile-num
  #"(0|\+353|353)(83|85|86|87|88|89)\d{7}")
(defn valid-num? [phone-number]
  (not (re-matches irish-mobile-num
                   phone-number)))

(def reader (transit/reader :json))

(defn json->cljs-message [json-message]
  (js->clj (as-> json-message m
                 (transit/read reader m)
                 (dissoc m "dest")
                 (update m "timestamp" time-c/from-string)
                 (update m "timestamp" time-c/to-local-date-time))
           :keywordize-keys true))