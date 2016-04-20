(ns sms-client.android.core
  (:require [reagent.core :as r :refer [atom]]
            [re-frame.core :refer [subscribe dispatch dispatch-sync]]
            [sms-client.handlers]
            [sms-client.subs]
            [sms-client.shared.ui :as ui]
            [sms-client.android.ui :as android-ui]
            [sms-client.utilities :as util]
            [sms-client.shared.scenes.message-scenes :as m-scenes]))

(def logo-img (js/require "./images/cljs.png"))

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
       [android-ui/button {:text     "Login"
                           :raised   true
                           :on-press #(dispatch
                                       [:set-phone-number
                                        @temp-number])}]]
      [android-ui/navigator
       {:initial-route   {:name "Messages" :index 1}
        :configure-scene (fn [_ _]
                           js/React.Navigator.SceneConfigs.FloatFromBottomAndroid)
        :render-scene    (fn [_ navigator] (r/as-element
                                             [ui/view {:style {:flex 1}}
                                              [android-ui/toolbar {:title   "Messages"
                                                                   :style   {:flex 1}
                                                                   :actions [{:icon    :message
                                                                              :onPress (fn [] (android-ui/show-dialog
                                                                                                {:title        "New-mesage"
                                                                                                 :input        {:hint     "new message?"
                                                                                                                :callback (fn [text] (str text))}
                                                                                                 :positiveText "add.."})) #_(ui/alert "new message")}]}]
                                              [ui/view {:style {:flex 1 :margin-top 53}}
                                               [m-scenes/message-list {:navigator navigator}]
                                               [ui/text "hello"]]]))

        :style           {:position "absolute"
                          :top      0
                          :left     0
                          :bottom   0
                          :right    0
                          :flex     1}}])))

#_[ui/view
   [ui/text "hello david how are you today?"]
   [ui/text "I am feeling good"]]

#_(let [greeting (subscribe [:get-greeting])]
    (fn []
      [ui/view {:style {:flex-direction "column" :margin 40 :align-items "center"}}
       [ui/text {:style {:font-size 30 :font-weight "100" :margin-bottom 20 :text-align "center"}} @greeting]
       [ui/image {:source logo-img
                  :style  {:width 80 :height 80 :margin-bottom 30}}]
       [ui/touchable-highlight {:style    {:background-color "#999" :padding 10 :border-radius 5}
                                :on-press #(ui/alert "HELLO!")}
        [ui/text {:style {:color "white" :text-align "center" :font-weight "bold"}} "press me"]]]))

(defn init []
  (dispatch-sync [:initialize-db])
  (dispatch [:retrieve-messages])
  (.registerComponent ui/app-registry "SmsClient" #(r/reactify-component app-root)))
