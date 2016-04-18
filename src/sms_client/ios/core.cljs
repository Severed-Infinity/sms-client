(ns sms-client.ios.core
  (:require [reagent.core :as r :refer [atom]]
            [re-frame.core :refer [subscribe dispatch dispatch-sync]]
            [sms-client.handlers]
            [sms-client.subs]
            [sms-client.shared.ui :as ui]
            [sms-client.ios.ui :as ios-ui]
            [sms-client.shared.scenes.message-scenes :as m-scenes]
            [sms-client.utilities :as util]))

#_(def logo-img (js/require "./images/cljs.png"))

(defn app-root []
  (let [phone-number (subscribe [:phone-number])
        temp-number  (subscribe [:temp-number])]
    (if (util/valid-num? @phone-number)
      [ui/view {:style {:flex            1
                        :justify-content :center
                        :align-items     :center
                        :height          150}}
       [ui/text {:style {:font-size   25
                         :font-weight "600"}}
        "Enter your Phone Number"]
       [ui/text-input {:placeholder    "Phone Number"
                       :style          {:font-size     16
                                        :color         "#777"
                                        :border-color  "#999"
                                        :border-style  "solid"
                                        :background-color
                                                       "#fff"
                                        :border-radius 4
                                        :border-width  1
                                        :min-height    40
                                        :margin-left   5
                                        :margin-right  5
                                        :margin-top    5
                                        :margin-bottom 5
                                        :padding-left  10
                                        :padding-right 10
                                        :padding-top   5}
                       :max-length     10
                       :value          @temp-number
                       :on-change-text #(dispatch
                                         [:set-temp-number %])}]
       [ios-ui/button {:on-press        #(dispatch
                                          [:set-phone-number @temp-number])
                       :style           {:font-size 20}
                       :container-style {:padding       10
                                         :height        40
                                         :overflow      :hidden
                                         :border-radius 4
                                         :background-color
                                                        "#fff"
                                         :margin-right  5
                                         :margin-top    5
                                         :margin-bottom 5}}
        "Login"]
       [ios-ui/keyboard-spacer]]
      [ios-ui/navigator
       {:initial-route
        {:title                 "Messages"
         ;TODO initial component to be message list - WIP
         ;:commponent must reactify a component function that takes a map of navigator
         ;TODO on right press callback to be a re-frame
         ; dispatch?
         :component             (m-scenes/message-list-comp)
         :right-button-title    "New"
         ;TODO contact lookup
         :on-right-button-press #(ios-ui/show-dialog
                                  {:text     "Enter Phone Number"
                                   :callback (fn [phone-number]
                                               (dispatch
                                                 [:new-chat
                                                  phone-number]))})}
        :style
        {:position "absolute"
         :top      0
         :left     0
         :bottom   0
         :right    0
         :flex     1}}])))

;TODO better handling of message fetching and updating
;TODO possible handling of app state
(defn init []
  (dispatch-sync [:initialize-db])
  #_(dispatch [:load-messages "hello"])
  (.registerComponent ui/app-registry "SmsClient" #(r/reactify-component app-root)))

(comment
  re-natal use-ios-device real
  re-natal use-component react-native-material-design
  re-natal use-component react-native-dialogs
  re-natal use-component react-native-button
  re-natal use-component react-native-keyboard-spacer
  re-natal use-figwheel
  react-native run-android
  react-native run-ios
  lein figwheel ios android)


