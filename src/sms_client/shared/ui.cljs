(ns sms-client.shared.ui
  (:require [reagent.core :as r]))

(set! js/window.React (js/require "react-native"))

(def scroll-view (r/adapt-react-class (.-ScrollView js/React)))
(def refresh-control (r/adapt-react-class (.-RefreshControl js/React)))
(def list-view (r/adapt-react-class (.-ListView js/React)))
(def app-registry (.-AppRegistry js/React))
(def text (r/adapt-react-class (.-Text js/React)))
(def text-input (r/adapt-react-class (.-TextInput js/React)))
(def view (r/adapt-react-class (.-View js/React)))
(def image (r/adapt-react-class (.-Image js/React)))
(def touchable-highlight (r/adapt-react-class (.-TouchableHighlight js/React)))

(defn alert [title]
  (.alert (.-Alert js/React) title))