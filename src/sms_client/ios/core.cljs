(ns sms-client.ios.core
  (:require [reagent.core :as r :refer [atom]]
            [re-frame.core :refer [subscribe dispatch dispatch-sync]]
            [sms-client.handlers]
            [sms-client.subs]
            [sms-client.shared.ui :as ui]
            [sms-client.ios.ui :as ios-ui]
            [sms-client.shared.scenes.message-scenes :as m-scenes]))

#_(def logo-img (js/require "./images/cljs.png"))

(defn app-root []
  [ios-ui/navigator
   {:initial-route
    {:title                 "Messages"
     ;TODO initial component to be message list - WIP
     ;:commponent must reactify a component function that takes a map of navigator
     ;TODO on right press callback to be a re-frame
     ; dispatch?
     :component             (m-scenes/message-list-comp)
     :right-button-title    "New"
     #_:on-right-button-press #_(ios-ui/show-dialog
                                 {:text     "New Message"
                                  :callback (fn [_] nil)})}
    :style
    {:position "absolute"
     :top      0
     :left     0
     :bottom   0
     :right    0
     :flex     1}}])

;TODO better handling of message fetching and updating
;TODO possible handling of app state
(defn init []
  (dispatch-sync [:initialize-db])
  (.registerComponent ui/app-registry "SmsClient" #(r/reactify-component app-root)

                      #_(re-natal use-ios-device real
                                  re-natal use-component react-native-material-design
                                  re-natal use-component react-native-dialogs
                                  re-natal use-component react-native-button
                                  re-natal use-figwheel
                                  react-native run-ios
                                  lein figwheel ios)))
