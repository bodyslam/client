(ns wh.landing-new.views
  (:require #?(:cljs [wh.pages.core :refer [load-and-dispatch]])
            [wh.components.activities.article-published :as article-published]
            [wh.components.activities.company-published :as company-published]
            [wh.components.activities.issue-published :as issue-published]
            [wh.components.activities.job-published :as job-published]
            [wh.components.activities.job-published :as job-published]
            [wh.components.attract-card :as attract-card]
            [wh.components.loader :as loader]
            [wh.components.newsletter :as newsletter]
            [wh.components.side-card.side-card :as side-cards]
            [wh.components.side-card.side-card-mobile :as side-cards-mobile]
            [wh.components.stat-card :as stat-card]
            [wh.components.tag-selector.tag-selector :as tag-selector]
            [wh.landing-new.subs :as subs]
            [wh.re-frame.subs :refer [<sub]]
            [wh.styles.landing :as styles]
            [wh.util :as util]))

(defmulti activity-card (juxt :verb :object-type))

(defmethod activity-card [:publish "company"] [activity]
  [company-published/card (:object activity) :publish])

(defmethod activity-card [:publish "job"] [activity]
  [job-published/card (:object activity) :publish])

(defmethod activity-card [:publish "issue"] [activity]
  [issue-published/card (:object activity) :publish])

(defmethod activity-card [:publish "article"] [activity]
  [article-published/card (:object activity) :publish])

(defmethod activity-card [:highlight "company"] [activity]
  [company-published/card (:object activity) :highlight])

(defmethod activity-card [:highlight "job"] [activity]
  [job-published/card (:object activity) :highlight])

(defmethod activity-card [:highlight "issue"] [activity]
  [issue-published/card (:object activity) :highlight])

(defmethod activity-card [:highlight "article"] [activity]
  [article-published/card (:object activity) :highlight])

(defmethod activity-card :default [_activity]
  nil)

(defn tag-picker []
  [:div (util/smc styles/card styles/card--tag-picker) "Tag picker"])

(defn split-into-3-groups [elms]
  (let [group-size (quot (count elms) 3)]
    [(take group-size elms)
     (->> elms
          (drop group-size)
          (take group-size))
     (drop (* 2 group-size) elms)]))

(defn page []
  (let [blogs        (<sub [::subs/top-blogs])
        users        (<sub [::subs/top-users])
        companies    (<sub [::subs/top-companies])
        jobs         (<sub [::subs/recent-jobs])
        issues       (<sub [::subs/recent-issues])
        tags         (<sub [::subs/top-tags])
        activities   (<sub [::subs/recent-activities])
        activities-loading? (<sub [::subs/recent-activities-loading?])
        logged-in?   (<sub [:user/logged-in?])
        vertical     (<sub [:wh/vertical])
        query-params (<sub [:wh/query-params])]
    [:div (util/smc styles/page)
     (when-not logged-in?
       [attract-card/all-cards {:logged-in? logged-in?
                                :vertical vertical}])
     [:div (util/smc styles/page__main)
      [:div (util/smc styles/side-column styles/side-column--left)
       [tag-selector/card-with-selector tags]
       [side-cards/top-ranking {:blogs        blogs
                                :companies    companies
                                :default-tab  :companies
                                :redirect     :homepage-new
                                :logged-in?   logged-in?
                                :query-params query-params}]
       [side-cards/top-ranking {:blogs        blogs
                                :companies    companies
                                :default-tab  :blogs
                                :redirect     :homepage-new
                                :logged-in?   logged-in?
                                :query-params query-params}]]
      (if activities-loading?
        [loader/loader styles/loader]
        (let [groups (split-into-3-groups activities)]
          (into [:div {:class styles/main-column}
                 [side-cards-mobile/top-content {:jobs jobs
                                                 :blogs blogs
                                                 :issues issues}]
                 (for [activity (nth groups 0)] ^{:key (:id activity)} [:div (activity-card activity)])
                 [stat-card/about-applications]
                 [newsletter/newsletter {:logged-in? logged-in?
                                         :type :landing}]
                 [stat-card/about-open-source]
                 (for [activity (nth groups 1)] ^{:key (:id activity)} [:div (activity-card activity)])
                 [stat-card/about-salary-increase]
                 (for [activity (nth groups 2)] ^{:key (:id activity)} [:div (activity-card activity)])])))
      [:div {:class styles/side-column}
       [side-cards/recent-jobs jobs]
       [side-cards/recent-issues issues]
       [side-cards/top-ranking-users users]]]]))
