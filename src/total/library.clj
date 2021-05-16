(ns total.library
  (:require
            [clojure.data.json :as json]
            [clojure.spec.alpha :as s]
            [orchestra.spec.test :as ostest]
            [total.book :as bk]
            [total.borrower :as br]
            [total.domain :as dom]))

(defn add-item [x xs]
  (if (some #{x} xs)
    xs
    (into () (cons x xs))))
(s/fdef add-item
        :args (s/or
                :is-brs (s/cat :x :unq/borrower :xs :unq/brs)
                :is-bks (s/cat :x :unq/book :xs :unq/bks))
        :ret (s/or :ret-brs :unq/brs
                   :ret-bks :unq/bks))

(defn remove-book [book books]
  (into () (filter #(not= book %) books)))
(s/fdef remove-book
        :args (s/cat :book :unq/book :books :unq/bks)
        :ret :unq/bks)

;(defn find-item [tgt coll f]
;  (let [result (filter (fn [x] (= (f x) tgt)) coll)]
;    (first result)))

;(defn find-item [tgt coll f]
;  (let [result (filter #(= (f %) tgt) coll)]
;    (first result)))

(defn find-item [target coll func]
  (let [result (for [item coll
                     :let [ct (func item)]
                     :when (= ct target)]
                 item)]
    (first result)))


(s/fdef find-item
        :args (s/or
                :is-brs (s/cat :target ::dom/name :coll :unq/brs :func ::dom/extract-fn-br-name)
                :is-bks (s/cat :target ::dom/title :coll :unq/bks :func ::dom/extract-fn-bk-title))
        :ret (s/or :found-br :unq/borrower
                   :found-bk :unq/book
                   :not-found nil?))

(defn get-books-for-borrower [borrower books]
  (into () (for [bk books
                 :let [cb (bk/get-borrower bk)]
                 :when (= cb borrower)]
             bk)))
(s/fdef get-books-for-borrower
        :args (s/cat :borrower :unq/borrower :books :unq/bks)
        :ret :unq/bks)

(defn- num-books-out [borrower books]
  (count (get-books-for-borrower borrower books)))
(s/fdef num-books-out
        :args (s/cat :borrower :unq/borrower :books :unq/bks)
        :ret int?)

(defn- not-maxed-out? [borrower books]
  (< (num-books-out borrower books) (br/get-max-books borrower)))
(s/fdef not-maxed-out?
        :args (s/cat :borrower :unq/borrower :books :unq/bks)
        :ret boolean?)

(defn- book-not-out? [book]
  (nil? (bk/get-borrower book)))
(s/fdef book-not-out?
        :args (s/cat :book :unq/book)
        :ret boolean?)

(defn- book-out? [book]
  (not (nil? (bk/get-borrower book))))
(s/fdef book-out?
        :args (s/cat :book :unq/book)
        :ret boolean?)

(defn check-out [name title borrowers books]
  (let [mbk (find-item title books bk/get-title)
        mbr (find-item name borrowers br/get-name)]
    (if (and (not= mbk nil) (not= mbr nil)
             (not-maxed-out? mbr books) (book-not-out? mbk))
      (let [new-book (bk/set-borrower mbk mbr)
            fewer-books (remove-book mbk books)]
        (add-item new-book fewer-books))
      books)))
(s/fdef check-out
        :args (s/cat :name ::dom/name
                     :title ::dom/title
                     :borrowers :unq/brs
                     :books :unq/bks)
        :ret :unq/bks)

(defn check-in [title books]
  (let [mbk (find-item title books bk/get-title)]
    (if (and (not= mbk nil) (book-out? mbk))
      (let [new-book (bk/set-borrower mbk nil)
            fewer-books (remove-book mbk books)]
        (add-item new-book fewer-books))
      books)))
(s/fdef check-in
        :args (s/cat :title ::dom/title :books :unq/bks)
        :ret :unq/bks)

(defn- my-key-reader
  [key]
  (cond
    (= key "maxBooks") :max-books
    (= key "borrower") :maybe-borrower
    :else (keyword key)))

(defn- my-key-writer
  [key]
  (cond
    (= key :max-books) "maxBooks"
    (= key :maybe-borrower) "borrower"
    :else (name key)))

(defn json-string-to-list [[error-string json-string]]
  (if (nil? error-string)
    (try
      [nil (into () (json/read-str json-string
                                   :key-fn my-key-reader))]
      (catch Exception e
        [(str (.getMessage e)) nil]))
    [error-string nil]))
(s/fdef json-string-to-list
        :args (s/cat :input (s/or :success-in (s/tuple nil? string?)
                                  :failure-in (s/tuple string? nil?)))
        :ret (s/or :is-json-brs (s/tuple nil? :unq/brs)
                   :is-json-bks (s/tuple nil? :unq/bks)
                   :is-error (s/tuple string? nil?)))

(defn collection-to-json-string [collection]
  (json/write-str collection
                  :key-fn my-key-writer))
(s/fdef collection-to-json-string
        :args (s/or :is-brs (s/cat :collection :unq/brs)
                    :is-bks (s/cat :collection :unq/bks))
        :ret string?)

(defn library-to-string [books borrowers]
  (str "Test Library: "
       (count books) " books; "
       (count borrowers) " borrowers."))
(s/fdef library-to-string
        :args (s/cat :books :unq/bks :borrowers :unq/brs)
        :ret string?)

(defn status-to-string [books borrowers]
  (str "\n"
       "--- Status Report of Test Library ---\n"
       "\n"
       (library-to-string books borrowers)
       "\n"
       (apply str (interpose "\n" (map bk/to-string books))) "\n\n"
       (apply str (interpose "\n" (map br/to-string borrowers))) "\n\n"
       "--- End of Status Report ---"
       "\n"))
(s/fdef status-to-string
        :args (s/cat :books :unq/bks :borrowers :unq/brs)
        :ret string?)

(ostest/instrument)
