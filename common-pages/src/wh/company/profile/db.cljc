(ns wh.company.profile.db
  (:require [#?(:clj clojure.spec.alpha
                :cljs cljs.spec.alpha) :as s]))

(s/def ::photo-uploading? boolean?)
(s/def ::updating? boolean?)
(s/def ::creating-tag? boolean?)

(s/def ::tag-search (s/map-of :wh.tag/type string?))
(s/def ::selected-tag-ids (s/map-of :wh.tag/type (s/coll-of :wh.tag/id)))

(defn ->tag
  [m]
  (-> m
      (update :type keyword)))

(defn ->company
  [m]
  (-> m
      (update :tags (partial map ->tag))))
