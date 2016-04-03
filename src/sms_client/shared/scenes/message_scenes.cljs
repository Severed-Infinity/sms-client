(ns sms-client.shared.scenes.message-scenes
  (:require [sms-client.shared.ui :as ui]
            [sms-client.ios.ui :as ios-ui]
            [sms-client.handlers :as handler]
            [reagent.core :as r]))

;TODO move messages to a more appropiate place
(def messages (r/atom {}))

;TODO if new chat is made from main add contact to list along with chat history
;TODO add a send/submit type button for text input
(defn message-chat [{navigator :navigator}]
  #_(.log js/console navigator)
  #_(.log js/console (handler/send-message "0862561423" "0851263571" "hello from my app"))
  [ui/view {:style {:height 1 :flex 1}}
   [ui/view {:style {:flex 0 :margin-top 64}}
    [ui/text {:style {:font-weight "100" :color "#777"}}
     "hello from chat"]]
   [ui/text-input {:placeholder     "Text Input"
                   :style           {:font-size    14
                                     :color        "#777"
                                     :border-color "#999"
                                     :border-style "solid"
                                     :border-width 1
                                     :height       35
                                     :padding      5}
                   :auto-capitalize :sentences
                   :keyboard-type   :default
                   :multiline       true}]
   [ios-ui/button {:on-press #()} "send"]])

(defn message-chat-comp [] (r/reactify-component message-chat))

#_(defn open-message-chat [{navigator :navigator}]
    (.log js/console "testing open-message-chat")
    (.log js/console navigator)
    #_(.push navigator (clj->js {:component (message-chat-comp) :title "chat"})))

(defn message-list-item [{navigator :navigator
                          contact   :contact}]
  ;TODO most recent message lookup
  ;TODO touchable on-press to pass contact to message-chat-scene with lookup through nav props?
  #_(.log js/console navigator)
  (let [contact-num    (key contact)
        recent-message (get (first (val contact)) "message")]
      [ui/touchable-highlight
       {:active-opacity 0.9
         :underlay-color "#ccc"
         :on-press       #(.push
                           navigator
                           (clj->js
                             {:component (message-chat-comp) :title contact-num}))}
       [ui/view {:style {:height              55
                         :flex-direction      "column"
                         :padding             5 :padding-left 15
                         :padding-right       15
                         :border-bottom-width 1
                         :border-color        "#eee"
                         :border-style        "solid"
                         :margin-left         30}}
        [ui/text {:style           {:font-weight "600" :font-size 14}
                  :number-of-lines 1} contact]
        [ui/text {:style           {:font-weight "100" :font-size 11 :color "#777"}
                  :number-of-lines 2} recent-message]]]))

#_(defn message-list-item-comp [] (r/reactify-component
                                    message-list-item))

(defn message-list [{navigator :navigator}]
  ;TODO correctly format and nest messages a map with number as identifier
  #_(.log js/console navigator)
  [ui/view {:style {:height 1 :flex 1}}
    ;:margin-top 64
   [ui/scroll-view {:style {:flex 1}
                    #_(:refresh-control
                        (r/as-element
                          (let)
                          [refresher-state (r/atom false)]
                          [ui/refresh-control
                           {:refreashing @refresher-state
                            :on-refresh  (r/set-state
                                           refresher-state false)}]))}
    #_(map
        (fn [message]
          ^{:key (str "contact-" (key message))}
          [message-list-item {:contact   message
                              :navigator navigator}])
        #_[ui/text (str message)]
        @messages)
     [ui/text "hello \nhello \nhello \nhello \nhello \nhello
               \nhello \nhello"]]])

(defn message-list-comp [] (r/reactify-component message-list))

