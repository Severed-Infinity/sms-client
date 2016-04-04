(ns sms-client.db
  (:require [schema.core :as s :include-macros true]))

;; schema of app-db
(def schema {:messages {s/Str [{:message   s/Str
                                :timestamp s/Any
                                :src       s/Str}]}})

;; initial state of app-db
(def app-db {:messages {"0871234567" [{:message   "hello test"
                                       :timestamp "some time"
                                       :src       "0871234567"}]}})
