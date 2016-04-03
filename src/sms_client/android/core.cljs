(ns sms-client.android.core
  (:require [reagent.core :as r :refer [atom]]
            [re-frame.core :refer [subscribe dispatch dispatch-sync]]
            [sms-client.handlers]
            [sms-client.subs]
            [sms-client.shared.ui :as ui]
            [sms-client.android.ui :as android-ui]
            [sms-client.shared.scenes.message-scenes :as m-scenes]))

;(set! js/React (js/require "react-native"))
;
;(def app-registry (.-AppRegistry js/React))
;(def text (r/adapt-react-class (.-Text js/React)))
;(def view (r/adapt-react-class (.-View js/React)))
;(def image (r/adapt-react-class (.-Image js/React)))
;(def touchable-highlight (r/adapt-react-class (.-TouchableHighlight js/React)))

(def logo-img (js/require "./images/cljs.png"))

(defn app-root []
  [android-ui/navigator {:initial-route   {:name "Messages" :index 1}
                         :configure-scene (fn [_ _]
                                            js/React.Navigator.SceneConfigs.FloatFromBottomAndroid)
                         :render-scene    (fn [_ navigator] (r/as-element [ui/view {:style {:flex 1}}
                                                                           [android-ui/toolbar {:title   "Messages"
                                                                                                :style   {:flex 1}
                                                                                                :actions [{:icon    "message"
                                                                                                           :show    "always"
                                                                                                           :onPress (fn [] (android-ui/show-dialog {:title        "New-mesage"
                                                                                                                                                    :input        {:hint     "new message?"
                                                                                                                                                                   :callback (fn [text] (str text))}
                                                                                                                                                    :positiveText "add.."})) #_(ui/alert "new message")}]}]
                                                                           [ui/view {:style {:flex 1 :margin-top 53}}
                                                                            [m-scenes/message-list {:navigator navigator}]]]))

                         :style           {:position "absolute"
                                           :top      0
                                           :left     0
                                           :bottom   0
                                           :right    0
                                           :flex     1}}]

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
          [ui/text {:style {:color "white" :text-align "center" :font-weight "bold"}} "press me"]]])))

(defn init []
  (dispatch-sync [:initialize-db])
  (.registerComponent ui/app-registry "SmsClient" #(r/reactify-component app-root)))
