(ns sms-client.subs
  (:require-macros [reagent.ratom :refer [reaction]])
  (:require [re-frame.core :refer [register-sub]]))

(register-sub
 :get-greeting
 (fn [db _]
   (reaction
    (get @db :greeting))))

(register-sub
  :get-messages
  (fn [db _]
    (reaction
      (get @db :messages))))

(register-sub
  :chat
  (fn [db chat]
    (reaction
      (get-in @db [:messages chat]))))