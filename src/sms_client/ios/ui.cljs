(ns sms-client.ios.ui
  (:require [reagent.core :as r]))

(set! js/window.React (js/require "react-native"))
(set! js/IOSButton (js/require "react-native-button"))
(set! js/KeyboardSpacer (js/require
                          "react-native-keyboard-spacer"))

(def navigator (r/adapt-react-class (.-NavigatorIOS js/React)))
(def keyboard-spacer (r/adapt-react-class js/KeyboardSpacer))

#_(def tab-bar
    (r/adapt-react-class (.-TabBarIOS js/React)))
#_(def tab-bar-item
    (r/adapt-react-class (.-TabBarIOS.Item js/React)))

(def button
    (r/adapt-react-class js/IOSButton))

(defn show-dialog [{text     :text
                    callback :callback}]
  (.prompt (.-AlertIOS js/React) text nil callback))

(defn show-action-sheet [options callback]
  (.showActionSheetWithOptions (.-ActionSheetIOS js/React)
                               (clj->js options)
                               callback))