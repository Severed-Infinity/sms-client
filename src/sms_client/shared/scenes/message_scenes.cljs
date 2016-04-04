(ns sms-client.shared.scenes.message-scenes
  (:require [sms-client.shared.ui :as ui]
            [sms-client.ios.ui :as ios-ui]
            [sms-client.handlers :as handler]
            [reagent.core :as r]
            [re-frame.core :refer [subscribe]]))

;TODO if new chat is made from main add contact to list along with chat history
;TODO add a send/submit type button for text input
(defn message-chat [{navigator :navigator}]
  #_(.log js/console navigator)
  #_(.log js/console (handler/send-message "0862561423" "0851263571" "hello from my app"))
  (let [route      (js->clj (get (r/props
                                   (r/current-component))
                                 :route)
                            :keywordize-keys true)
        pass-props (:pass-props route)
        chat       (second (:chat pass-props))]
    [ui/view {:style {:height 1 :flex 1}}
     [ui/view {:style {:flex 1 :margin-top 64}}
      [ui/text {:style {:font-weight "100" :color "#777"}}
       (str chat)]]
     [ui/view {:style {:flex-direction   :row
                       :flex-wrap        :nowrap
                       :justify-content  :space-around
                       :align-items      :flex-start
                       :background-color "#eee"}}
      [ui/text-input {:placeholder     "Text Input"
                      :style           {:font-size     20
                                        :color         "#777"
                                        :border-color  "#999"
                                        :border-style  "solid"
                                        :background-color
                                                       "#fff"
                                        :border-radius 4
                                        :border-width  1
                                        :height        40
                                        :margin-left   5
                                        :margin-right  5
                                        :margin-top    2
                                        :margin-bottom 2
                                        :padding       10
                                        :flex          1}
                      :container-style {:padding 10}
                      :auto-capitalize :sentences
                      :keyboard-type   :default
                      :multiline       true}]
      ;TODO dispatch send to server
      [ios-ui/button {:on-press        #()
                      :style           {:font-size 20
                                        :flex      1}
                      :container-style {:padding       10
                                        :height        40
                                        :overflow      :hidden
                                        :border-radius 4
                                        :background-color
                                                       "#fff"
                                        :margin-right  5
                                        :margin-top    2
                                        :margin-bottom 2}}
       "send"]]
     [ios-ui/keyboard-spacer]]))

(defn message-chat-comp [] (r/reactify-component message-chat))

#_(defn open-message-chat [{navigator :navigator}]
    (.log js/console "testing open-message-chat")
    (.log js/console navigator)
    #_(.push navigator (clj->js {:component (message-chat-comp) :title "chat"})))

(defn message-list-item [{navigator :navigator
                          chat      :chat}]
  ;TODO most recent message lookup
  ;TODO touchable on-press to pass chat to message-chat-scene with lookup through nav props?
  ;TODO replace on-press function with re-frame dispatch
  #_(.log js/console navigator)
  (let [contact-num    (key chat)
        recent-message (get (first (val chat)) :message)]
    [ui/touchable-highlight
     {:active-opacity 0.9
      :underlay-color "#ccc"
      :on-press       #(.push
                        navigator
                        (clj->js
                          {:component  (message-chat-comp)
                           :title      contact-num
                           :pass-props {:chat chat}}))}
     [ui/view {:style {:height              55
                       :flex-direction      "column"
                       :padding             5 :padding-left 15
                       :padding-right       15
                       :border-bottom-width 1
                       :border-color        "#eee"
                       :border-style        "solid"
                       :margin-left         30}}
      [ui/text {:style           {:font-weight "600"
                                  :font-size   20}
                :number-of-lines 1}
       contact-num]
      [ui/text {:style           {:font-weight "100"
                                  :font-size   14
                                  :color       "#777"}
                :number-of-lines 2}
       recent-message]]]))

#_(defn message-list-item-comp [] (r/reactify-component
                                    message-list-item))

(defn message-list [{navigator :navigator}]
  ;TODO correctly format and nest messages a map with number as identifier
  #_(.log js/console navigator)
  (let [messages (subscribe [:get-messages])]
    (fn []
      [ui/view {:style {:height 1 :flex 1}}
       ;:margin-top 64
       ;TODO refresh-control to use dispatch call
       [ui/scroll-view {:style {:flex 1}
                        #_(:refresh-control
                            (r/as-element)
                            (let
                              [refresher-state (r/atom false)]
                              [ui/refresh-control
                               {:refreashing @refresher-state
                                :on-refresh  (r/set-state
                                               refresher-state false)}]))}
        (for [message @messages]
          ^{:key (key message)}
          [message-list-item
           {:navigator navigator
            :chat      message}])
        #_(.log js/console "logging messages: \n" @messages)
        #_[ui/text (:message (first (get @messages
                                         "0871234567")))]]])))


(defn message-list-comp [] (r/reactify-component message-list))

