(ns sms-client.handlers
  (:require
    [re-frame.core :refer [register-handler after]]
    [schema.core :as s :include-macros true]
    [sms-client.db :refer [app-db schema]]
    [cljs-http.client :as http]
    [cljs.core.async :refer [<! alts!]]
    [cognitect.transit :as transit])
  (:require-macros [cljs.core.async.macros :refer [go]]))

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

;-- Networking --------------------------------------------------------------

;TODO replace with re-frame handlers
(def base-url "http://sfinity-server.herokuapp.com")
;172.23.23.150
(def local-host "http://localhost:3033")
(def reader (transit/reader :json))

(defn sort-messages [messages response-messages]
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
    (go (let [response           (<! (http/get (str local-host "/user/" contact "/messages")))]
            new-messages       (transit/read reader (:body response))
            formatted-messages (vec (map #(transit/read reader %) new-messages)))
        (.log js/console (str "response body " formatted-messages))
        (if (not (empty? formatted-messages))
          (sort-messages messages formatted-messages))))

(register-handler
  :load-messages
  (fn [db [_ number]]
    (with-out-str number)
    #_(go
       (let [response (<! (http/get (str local-host
                                         "/user/" number
                                         "/messages")))
             new-messages (transit/read reader (:body response))
             formatted-messages (vec (map #(transit/read reader %) new-messages))]
         (if (not (empty? formatted-messages))
           (sort-messages db formatted-messages))))))


(defn send-message [src dest message]
  (go (go (<! (http/post (str local-host "/user/" src "/message")
                         #_{:form-params {:src src :dest dest :message message}}
                         {:multipart-params {:src src :dest dest :message message}})))))