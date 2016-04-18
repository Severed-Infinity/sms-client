(ns sms-client.handlers
  (:require
    [re-frame.core :refer [register-handler after
                           dispatch subscribe]]
    [schema.core :as s :include-macros true]
    [sms-client.db :refer [app-db schema]]
    [cljs-http.client :as http]
    [cljs.core.async :refer [<! alts! poll! take! timeout]]
    [cognitect.transit :as transit]
    [cljs-time.core :as time]
    [cljs-time.coerce :as time-c]
    [clojure.walk]
    [sms-client.utilities :as util])
  (:require-macros [cljs.core.async.macros :refer [go
                                                   go-loop]]
                   [reagent.ratom :refer [reaction]]))

;; -- Middleware ------------------------------------------------------------
;;
;; See https://github.com/Day8/re-frame/wiki/Using-Handler-Middleware
;;
(defn check-and-throw
  "throw an exception if db doesn't match the schema."
  [a-schema db]
  (if-let [problems (s/check a-schema db)]
    (throw (js/Error. (str "schema check failed: " problems)))))

(def validate-schema-mw
  (after (partial check-and-throw schema)))

;; -- Handlers --------------------------------------------------------------

(register-handler
  :initialize-db
  validate-schema-mw
  (fn [_ _]
    app-db))

(register-handler
  :set-greeting
  validate-schema-mw
  (fn [db [_ value]]
    (assoc db :greeting value)))

(register-handler
  :set-phone-number
  validate-schema-mw
  (fn [db [_ value]]
    (assoc db :phone-number value)))

(register-handler
  :set-temp-number
  validate-schema-mw
  (fn [db [_ value]]
    (assoc db :temp-number value)))

(register-handler
  :new-chat
  validate-schema-mw
  (fn [db [_ value]]
    (let [val->keyword (keyword value)]
      (if-not (get-in db [:messages val->keyword])
        (assoc-in db [:messages val->keyword] [])
        db))))

(register-handler
  :set-refresher-state
  validate-schema-mw
  (fn [db [_ value]]
    (assoc db :refresher-state value)))

;-- Networking --------------------------------------------------------------

;TODO replace with re-frame handlers
(def base-url "http://sfinity-server.herokuapp.com")
;172.23.23.150 home
;10.95.230.181 college
;192.168.43.177 adams phone
;FIXME change based on locahostl ip
(def local-host "http://172.23.23.150:3033")
(def reader (transit/reader :json))



#_(defn sort-messages [messages response-messages]
    #_(.log js/console @messages)
    #_(.log js/console response-messages)
    (when (seq response-messages)
      (let [next-message (first response-messages)]
        #_(.log js/console next-message)
        (if (contains? @messages (get next-message "src"))
          (swap! messages update (get next-message "src")
                 conj (get next-message "message"))
          (swap! messages assoc (get next-message "src")
                 (conj [] (dissoc next-message "dest")))))))

#_(defn get-messages [contact messages]
    (go (let [response (<! (http/get (str local-host "/user/" contact "/messages")))]
          new-messages (transit/read reader (:body response))
          formatted-messages (vec (map #(transit/read reader %) new-messages)))
        (.log js/console (str "response body " formatted-messages))
        (if (not (empty? formatted-messages))
          (sort-messages messages formatted-messages))))

(register-handler
  :new-message
  (fn [db [_ dest new-message]]
    (let [formatted-message (util/json->cljs-message
                              new-message)
          chat              (subscribe [:chat dest])]
      (assoc-in db [:messages dest]
                (into [] (concat
                           [(clojure.walk/keywordize-keys
                              formatted-message)]
                           @chat))))))

(register-handler
  :add-new-messages
  (fn [db [_ response]]
    (let [new-messages       (transit/read reader response)
          formatted-messages (vec (map #(transit/read reader %) new-messages))]
      (dispatch [:set-refresher-state false])
      (if (not (empty? formatted-messages))
        (do (.log js/console formatted-messages)
          (loop [messasges formatted-messages]
            (let [message (first messasges)]
              (.log js/console message)
              (dispatch [:new-chat (get message "src")])
              #_(update-in db [:messages
                               (keyword (get message "src"))]
                           message))
            (recur (rest messasges)))
          #_(reduce #(do
                      (dispatch [:new-chat (get % "src")])
                      (dispatch [:new-message (keyword
                                                (get % "src"))
                                 %]))
                    #_(.log js/console %)
                    formatted-messages))))
    (assoc db :refresher-state false)))

(register-handler
  :retrieve-messages
  (fn [db [_]]
    (go-loop
      []
      (dispatch [:set-refresher-state true])
      (.log js/console "retrieving messages")
      (let [phone-number (subscribe [:phone-number])]
        (if-not (empty? @phone-number)
          (when-let [response (:body (<! (http/get (str
                                                     local-host
                                                     "/user/" @phone-number
                                                     "/messages"))))]
            (dispatch [:add-new-messages response]))))
      (<! (timeout 60000))
      (recur))
    (assoc db :refresher-state false)))

(register-handler
  :send-message
  (fn [db [_ src dest message]]
    (go
      (when-let [response (:body (<! (http/post (str
                                                  local-host "/user/" src "/message")
                                                #_{:form-params {:src src :dest dest :message message}}
                                                {:multipart-params {:src src :dest dest :message message}})))]
        (dispatch [:new-message (keyword dest) response])))
    (assoc db :current-text-body "")))

(register-handler
  :set-message-body
  (fn [db [_ value]]
    (assoc db :current-text-body value)))