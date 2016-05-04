(ns sms-client.shared.scenes.message-scenes
  (:require [sms-client.shared.ui :as ui]
            [sms-client.ios.ui :as ios-ui]
            [sms-client.handlers :as handler]
            [reagent.core :as r]
            [re-frame.core :refer [subscribe dispatch]]
            [cljs-time.coerce :as time-c]
            [sms-client.utilities :as util])
  (:require-macros [reagent.ratom :refer [reaction]]))

;TODO if new chat is made from main add contact to list along with chat history
;TODO add a send/submit type button for text input
(defn message-chat [{navigator :navigator}]
  #_(.log js/console navigator)
  #_(.log js/console (handler/send-message "0862561423" "0851263571" "hello from my app"))
  ;TODO subscribe to chat rather than parse from navigator
  (let [route           (js->clj (get (r/props
                                        (r/current-component))
                                      :route)
                                 :keywordize-keys true)
        pass-props      (:pass-props route)
        destination-num (first (:chat pass-props))
        phone-number    (subscribe [:phone-number])
        current-text    (subscribe [:get-current-text-body])
        chat2           (subscribe [:chat
                                    (keyword
                                      destination-num)])]
    [ui/view {:style {:height 1 :flex 1}}
     [ui/view {:style {:flex 1 :margin-top 64}}
      (doall
        (map
          (fn [message]
            ^{:key (hash message)}
            [ui/text {:style           {:align-self    (if (=
                                                             @phone-number (:src message))
                                                         :flex-end
                                                         :flex-start)
                                        :background-color
                                                       (if (=
                                                             @phone-number (:src message))
                                                         "#66CD00"
                                                         "#ddd")
                                        :max-width     (* 8
                                                          (count
                                                            (:message
                                                              message)))
                                        :margin-top    8
                                        :margin-right  15
                                        :margin-left   15
                                        :padding       10
                                        :border-radius 10
                                        :overflow      :hidden
                                        :color         (if (=
                                                             @phone-number (:src message))
                                                         "#fff"
                                                         "#000")}
                      :container-style {:border-radius 10
                                        :overflow      :hidden}}

             (:message message)])
          (sort :timestamp @chat2)))
      [ui/text {:style {:font-weight "100"
                        :color       "#777"}}
       (str destination-num "\n\n"
            @chat2 "\n\n" @current-text)]]
     [ui/view {:style {:flex-direction   :row
                       :flex-wrap        :nowrap
                       :justify-content  :space-around
                       :align-items      :flex-start
                       :background-color "#eee"
                       :min-height       40}}
      [ui/text-input {:placeholder     "Message"
                      :style           {:font-size     16
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
                                        :padding-top   5
                                        :flex          1}
                      :auto-capitalize :sentences
                      :keyboard-type   :default
                      :value           @current-text
                      :on-change-text  #(dispatch
                                         [:set-message-body %])
                      :multiline       true}]
      ;TODO dispatch send to server
      [ios-ui/button {:on-press        #(dispatch
                                         [:send-message
                                          @phone-number
                                          destination-num
                                          @current-text])
                      :style           {:flex      1
                                        :font-size 20}
                      :container-style {:padding       10
                                        :height        40
                                        :overflow      :hidden
                                        :border-radius 4
                                        :background-color
                                                       "#fff"
                                        :margin-right  5
                                        :margin-top    5
                                        :margin-bottom 5}}
       "send"]]
     [ios-ui/keyboard-spacer]]))

(defn message-chat-comp [] (r/reactify-component message-chat))

(defn message-list-item [{navigator :navigator
                          chat      :chat}]
  ;TODO most recent message lookup
  ;TODO touchable on-press to pass chat to message-chat-scene with lookup through nav props?
  ;TODO replace on-press function with re-frame dispatch
  #_(.log js/console navigator)
  (let [contact-num    (name (key chat))
        recent-message (get (first (val chat)) :message)]
    [ui/touchable-highlight
     (if util/ios?
       {:active-opacity 0.9
        :underlay-color "#ccc"
        :on-press       #(.push
                          navigator
                          (clj->js
                            {:component  (message-chat-comp)
                             :title      contact-num
                             :pass-props {:chat chat}}))}
       {:active-opacity 0.9
        :underlay-color "#ccc"
        :on-press       #(.push
                          navigator
                          (clj->js
                            {:route message-chat
                             :index 2}))})
     [ui/view {:style {:min-height          55
                       :flex-direction      "column"
                       :padding             10
                       :padding-left        15
                       :padding-right       15
                       :border-bottom-width 1
                       :border-color        "#eee"
                       :border-style        "solid"
                       :margin-left         30}}
      [ui/text {:style           {:font-weight "700"
                                  :font-size   16}
                :number-of-lines 1}
       contact-num]
      [ui/text {:style           {:font-weight "400"
                                  :color       "#777"}
                :number-of-lines 2}
       recent-message]]]))

(defn message-list [{navigator :navigator}]
  ;FIXME sort order of messages
  #_(.log js/console navigator)
  (let [messages        (subscribe [:get-messages])
        phone-number    (subscribe [:phone-number])
        refresher-state (subscribe [:refresher-state])]
    (fn []
      [ui/view {:style {:flex 1}}
       ;:margin-top 64
       [ui/scroll-view {:style {:flex 1}
                        #_(:refresh-control
                            (r/as-element
                              [ui/refresh-control
                               {:refreashing @refresher-state
                                :on-refresh  #(dispatch
                                               [:retrieve-messages])}]))}
        (for [message @messages]
          ^{:key (name (key message))}
          [message-list-item
           {:navigator navigator
            :chat      message}])
        #_[ui/text (str @messages "\n" @phone-number
                      @(subscribe [:refresher-state]))]]])))

(defn message-list-comp [] (r/reactify-component message-list))

