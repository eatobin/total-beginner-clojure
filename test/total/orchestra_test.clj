(ns total.orchestra-test
  (:require [clojure.test :refer [deftest is]]
            [clojure.spec.alpha :as s]))

(defn simple-fn [x]
  x
  1)

(s/fdef simple-fn
        :args (s/cat :x :simple/string)
        :ret :simple/tiny-int)

(deftest spec-fail-test
  (is (= 1 (simple-fn "t")) "Just testing simple-fn"))
