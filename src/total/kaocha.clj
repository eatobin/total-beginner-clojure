(ns total.kaocha
  (:require [clojure.spec.alpha :as s]))

(s/def :simple/int int?)
(s/def :simple/string string?)
(s/def :simple/tiny-int (s/int-in 1 10))
