(ns total.specs
  (:require [total.borrower :as br]
            [total.book :as bk]
            [total.library :as lib]
            [total.main :as mn]
            [clojure.spec.alpha :as s]
            [clojure.spec.test.alpha :as stest]
            [clojure.spec.gen.alpha :as sgen]
            [clojure.test.check :as tc]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as prop]))

(defn ascending?
  "clojure.core/sorted? doesn't do what we might expect, so we write our
  own function"
  [coll]
  (every? (fn [[a b]] (<= a b))
          (partition 2 1 coll)))
(def property
  (prop/for-all [v (gen/vector gen/int)]
                (let [s (sort v)]
                  (and (= (count v) (count s))
                       (ascending? s)))))
(tc/quick-check 100 property)
(def bad-property
  (prop/for-all [v (gen/vector gen/int)]
                (ascending? v)))
(tc/quick-check 100 bad-property)

(gen/sample gen/int)
(gen/sample gen/int 20)
(take 1 (gen/sample-seq gen/int))
(gen/sample (gen/vector gen/nat))
(gen/sample (gen/list gen/boolean))
(gen/sample (gen/tuple gen/nat gen/boolean gen/ratio))
(gen/sample (gen/tuple gen/nat gen/boolean gen/ratio))

(gen/sample (gen/fmap set (gen/vector gen/int)))

(def keyword-vector (gen/such-that not-empty (gen/vector gen/keyword)))
(def vec-and-elem
  (gen/bind keyword-vector
            (fn [v] (gen/tuple (gen/elements v) (gen/return v)))))

(gen/sample vec-and-elem 8)

(sgen/generate (s/gen int?))

(s/explain ::br/borrower {::br/name "me" ::br/max-books 4})
(s/exercise-fn `br/make-borrower)
(stest/check `br/make-borrower)
(stest/check `br/get-name)
(stest/check `br/set-name)

(s/explain ::bk/book
           {::bk/title          "T"
            ::bk/author         "a"
            ::bk/maybe-borrower {::br/name      "Nn",
                                 ::br/max-books 333}})
(s/exercise-fn `bk/make-book)
(stest/check `bk/make-book)
(s/conform ::bk/book {::bk/title          "T"
                      ::bk/author         "a"
                      ::bk/maybe-borrower {::br/name      "Nn",
                                           ::br/max-books 333}})
(s/conform ::bk/book {::bk/title          "T"
                      ::bk/author         "a"
                      ::bk/maybe-borrower nil})

(s/exercise-fn `lib/add-item)
(stest/check `lib/add-item)
(s/exercise-fn `lib/remove-book)
(stest/check `lib/remove-book)
(s/exercise-fn `lib/find-item)
(stest/check `lib/find-item)
(s/conform ::lib/extract-fn-br-name br/get-name)
(s/explain ::lib/extract-fn-br-name br/get-name)
(s/conform ::lib/extract-fn-bk-title bk/get-title)
(s/explain ::lib/extract-fn-bk-title bk/get-title)
(stest/check `lib/get-books-for-borrower)
(s/exercise-fn `lib/get-books-for-borrower)
(stest/check `lib/num-books-out)
(s/exercise-fn `lib/num-books-out)
(stest/check `lib/not-maxed-out?)
(stest/check `lib/book-not-out?)
(stest/check `lib/book-out?)
(stest/check `lib/check-out)
(stest/check `lib/check-in)
(stest/check `mn/read-file-into-json-string)
(stest/check `lib/json-string-to-brs)
(stest/check `lib/json-string-to-bks)
(stest/check `lib/brs-to-json-string)
(stest/check `lib/library-to-string)
(stest/check `lib/status-to-string)
(s/exercise-fn `lib/status-to-string)
