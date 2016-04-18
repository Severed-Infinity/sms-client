(ns sms-client.subs
  (:require-macros [reagent.ratom :refer [reaction]])
  (:require [re-frame.core :refer [register-sub]]
            [sms-client.utilities :as util]
            [cljs-time.core :as time]))

(register-sub
  :get-greeting
  (fn [db _]
    (reaction
      (get @db :greeting))))

(register-sub
  :get-current-text-body
  (fn [db _]
    (reaction
      (get @db :current-text-body))))

(register-sub
  :get-messages
  (fn [db _]
    (let [messages (reaction (get @db :messages))]
      (reaction
        (into (sorted-map-by :timestamp)
              @messages)))))

(register-sub
  :chat
  (fn [db [_ chat]]
    (let [chat-list (reaction (get-in @db
                                      [:messages chat]
                                      :not-found))]
      (reaction
        @chat-list
        #_(apply vector
                 (sort-by (comp :timestamp second) <
                          @chat-list))))))

(register-sub
  :phone-number
  (fn [db _]
    (reaction
      (get @db :phone-number))))

(register-sub
  :temp-number
  (fn [db _]
    (reaction
      (get @db :temp-number))))