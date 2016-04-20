(ns sms-client.utilities
  (:require [clojure.walk :as walk]
            [cognitect.transit :as transit]
            [cljs-time.coerce :as c-time]))

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
  (walk/keywordize-keys
    (js->clj (as-> json-message m
                   (transit/read reader m)
                   (dissoc m "dest")
                   (update m "timestamp" c-time/to-local-date-time
                           #_(get m "timestamp"))))))

(defn format-response [response]
  (walk/keywordize-keys
    #_(get-in [response "messages"])
    (js->clj (transit/read reader response))))

