(ns sms-client.db
  (:require [schema.core :as s :include-macros true]
            [cljs-time.core :as time]))

;; schema of app-db
(def schema {:messages          {s/Keyword [{:message   s/Str
                                             :timestamp s/Any
                                             :src       s/Str}]}
             :phone-number      s/Str
             :greeting          s/Any
             :current-text-body s/Str
             :temp-number       s/Str
             :refresher-state   s/Bool})

;; initial state of app-db
(def app-db {:messages #_(into (sorted-map-by :timestamp))
                                {:0871234567
                                 [{:message   "hello test reply"
                                   :timestamp (time/now)
                                   :src       "0862561423"}
                                  {:message   "hello test"
                                   :timestamp (time/epoch)
                                   :src       "0871234567"}]
                                 :0851238709
                                 [{:message   "hello test new number"
                                   :timestamp (time/now)
                                   :src       "0862561423"}]}
             :phone-number      ""
             :greeting          "hello"
             :current-text-body ""
             :temp-number       ""
             :refresher-state   false})
