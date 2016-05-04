(ns sms-client.android.ui
  (:require [reagent.core :as r]))

(set! js/React (js/require "react-native"))
(set! js/MaterialDesign (js/require "react-native-material-design"))
(set! js/AndroidDialog (js/require "react-native-dialogs"))

(def navigator
  (r/adapt-react-class (.-Navigator js/React)))
(def toolbar
  (r/adapt-react-class (.-Toolbar js/MaterialDesign)))

(def button
  (r/adapt-react-class (.-Button js/MaterialDesign)))
(def card
  (r/adapt-react-class (.-Card js/MaterialDesign)))
(def card-media
  (r/adapt-react-class (.-Card.Media js/MaterialDesign)))
(def card-body
  (r/adapt-react-class (.-Card.Body js/MaterialDesign)))
(def card-actions
  (r/adapt-react-class (.-Card.Actions js/MaterialDesign)))

(def drawer-layout
  (r/adapt-react-class (.-DrawerLayoutAndroid js/React)))
(def drawer
  (r/adapt-react-class (.-Drawer js/MaterialDesign)))
(def drawer-header
  (r/adapt-react-class (.-Drawer.Header js/MaterialDesign)))
(def drawer-section
  (r/adapt-react-class (.-Drawer.Section js/MaterialDesign)))

(defn show-dialog [props]
  (let [dialog (new js/AndroidDialog)
        {:keys [title input positiveText]} props]
    (.prompt dialog positiveText input title)
    #_(.set dialog (clj->js props))
    #_(.show dialog)))
