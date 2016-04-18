(ns sms-client.utilities)

(set! js/window.React (js/require "react-native"))

(def platform-os (.-OS (.-Platform js/React)))
(def ios? (= platform-os "ios"))
(def android? (= platform-os "android"))

(def irish-mobile-num
  #"(0|\+353|353)(83|85|86|87|88|89)\d{7}")
(defn valid-num? [phone-number]
  (not (re-matches irish-mobile-num
                   phone-number)))

(def comp-time
  (fn [x y]
    (> (:timestamp x) (:timestamp y))))