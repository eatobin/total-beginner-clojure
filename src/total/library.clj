(ns total.library
  (:require [total.domain :as dom]
            [total.borrower :as br]
            [total.book :as bk]
            [cheshire.core :as json]
            [clojure.spec.alpha :as s]
            [orchestra.spec.test :as ostest]))

(defn add-item [x xs]
  (if (some #{x} xs)
    xs
    (into () (cons x xs))))
(s/fdef add-item
  :args (s/or
         :is-brs (s/cat :x ::dom/borrower :xs ::dom/brs)
         :is-bks (s/cat :x ::dom/book :xs ::dom/bks))
  :ret (s/or :ret-brs ::dom/brs
             :ret-bks ::dom/bks))

(defn remove-book [book books]
  (into () (filter #(not= book %) books)))
(s/fdef remove-book
  :args (s/cat :book ::dom/book :books ::dom/bks)
  :ret ::dom/bks)

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
(s/def br/get-name ::dom/extract-fn-br-name)
(s/def bk/get-title ::dom/extract-fn-bk-title)
(s/fdef find-item
  :args (s/or
         :is-brs (s/cat :target ::dom/name :coll ::dom/brs :func ::dom/extract-fn-br-name)
         :is-bks (s/cat :target ::dom/title :coll ::dom/bks :func ::dom/extract-fn-bk-title))
  :ret (s/or :found-br ::dom/borrower
             :found-bk ::dom/book
             :not-found nil?))

(defn get-books-for-borrower [borrower books]
  (into () (for [bk books
                 :let [cb (bk/get-borrower bk)]
                 :when (= cb borrower)]
             bk)))
(s/fdef get-books-for-borrower
  :args (s/cat :borrower ::dom/borrower :books ::dom/bks)
  :ret ::dom/bks)

(defn- num-books-out [borrower books]
  (count (get-books-for-borrower borrower books)))
(s/fdef num-books-out
  :args (s/cat :borrower ::dom/borrower :books ::dom/bks)
  :ret int?)

(defn- not-maxed-out? [borrower books]
  (< (num-books-out borrower books) (br/get-max-books borrower)))
(s/fdef not-maxed-out?
  :args (s/cat :borrower ::dom/borrower :books ::dom/bks)
  :ret boolean?)

(defn- book-not-out? [book]
  (nil? (bk/get-borrower book)))
(s/fdef book-not-out?
  :args (s/cat :book ::dom/book)
  :ret boolean?)

(defn- book-out? [book]
  (not (nil? (bk/get-borrower book))))
(s/fdef book-out?
  :args (s/cat :book ::dom/book)
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
               :borrowers ::dom/brs
               :books ::dom/bks)
  :ret ::dom/bks)

(defn check-in [title books]
  (let [mbk (find-item title books bk/get-title)]
    (if (and (not= mbk nil) (book-out? mbk))
      (let [new-book (bk/set-borrower mbk nil)
            fewer-books (remove-book mbk books)]
        (add-item new-book fewer-books))
      books)))
(s/fdef check-in
  :args (s/cat :title ::dom/title :books ::dom/bks)
  :ret ::dom/bks)

(defn json-string-to-list [json-string fields]
  (if (= json-string "File read error")
    "File read error"
    (let [json-str (try (doall (json/parse-string json-string fields))
                        (catch Exception _ nil))]
      (if (nil? json-str)
        "JSON parse error"
        (into () json-str)))))
(s/fdef json-string-to-list
  :args (s/cat :json-string string? :fields map?)
  :ret (s/or :is-json-brs ::dom/brs
             :is-json-bks ::dom/bks
             :is-error string?))

(defn collection-to-json-string [collection]
  (json/generate-string collection {:key-fn (fn [k] (name k))}))
(s/fdef collection-to-json-string
  :args (s/or :is-brs (s/cat :collection ::dom/brs)
              :is-bks (s/cat :collection ::dom/bks))
  :ret string?)

(defn library-to-string [books borrowers]
  (str "Test Library: "
       (count books) " books; "
       (count borrowers) " borrowers."))
(s/fdef library-to-string
  :args (s/cat :books ::dom/bks :borrowers ::dom/brs)
  :ret string?)

(defn status-to-string [books borrowers]
  (str "\n"
       "--- Status Report of Test Library ---\n"
       "\n"
       (library-to-string books borrowers)
       "\n"
       (apply str (interpose "\n" (map bk/book-to-string books))) "\n\n"
       (apply str (interpose "\n" (map br/borrower-to-string borrowers))) "\n\n"
       "--- End of Status Report ---"
       "\n"))
(s/fdef status-to-string
  :args (s/cat :books ::dom/bks :borrowers ::dom/brs)
  :ret string?)

(ostest/instrument)
