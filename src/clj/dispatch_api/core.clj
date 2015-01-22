(ns dispatch-api.core
  (:require 
    [clj-http.client :as client]
    [clojure.set :refer [intersection]]
    [dispatch-api.config :refer [get-req 
                                 post-req
                                 update-req
                                 delete-req
                                 auth-headers
                                 get-headers
                                 post-headers]
                         :as conf]
    [slingshot.slingshot :refer [try+]]))


(def base-url   "https://api-sandbox.dispatch.me")
(def url #(str base-url %))

;----------------------users-----------------------------------------------

(defn whoami []
  (let [url             (url "/v1/me")
        payload         (merge (get-headers)
                               (auth-headers))
        resp            (get-req url payload)]
    (-> resp :body :user)))

(defn get-users [& {:keys [fltr]}]
  (let [url             (url "/v1/users")
        payload         (merge (get-headers) 
                               (when fltr {:query-params {:filter fltr}})
                               (auth-headers))
        resp            (get-req url payload)]
    (-> resp :body :users)))

(defn delete-user [id]
  (let [url             (url (str "/v1/users/" id))
        payload         (merge (post-headers)
                               (auth-headers))
        resp            (delete-req url payload)]
    (:body resp)))

(defn get-technicians [& {:keys [fltr]}]
  (let [roles           {:by_user_roles "technician"}]
    (get-users :fltr (merge roles fltr))))

(defn get-dispatchers [& {:keys [fltr]}]
  (let [roles           {:by_user_roles "dispatcher"}]
    (get-users :fltr roles)))

;---------------------------organizations----------------------------------

(defn get-organizations [& {:keys [fltr]}]
  (let [url             (url "/v1/organizations")
        payload         (merge (get-headers) 
                               (if fltr {:query-params {:filter fltr}})
                               (auth-headers))
        resp            (get-req url payload)]
    (-> resp :body :organizations)))

;--------------addresses-----------------------------------------------------

(defn get-addresses [& {:keys [fltr]}]
  (let [url             (url "/v1/addresses")
        payload         (merge (get-headers)
                               (if fltr {:query-params {:filter fltr}})
                               (auth-headers))
        resp            (get-req url payload)]
    (-> resp :body :addresses)))

(defn add-address [address & other]
  (let [url             (url "/v1/addresses")
        payload         (merge (post-headers) 
                               (auth-headers)
                               {:form-params address})
        resp            (post-req url payload)]
    (-> resp :body :address)))

(defn update-address [address]
  (let [url             (url (str "/v1/addresses/" (:id address)))
        payload         (merge (get-headers)
                               (auth-headers)
                               {:form-params (dissoc address :id)})
        resp            (update-req url payload)]
    (-> resp :body :address)))

(defn delete-address [id]
  (let [url             (url (str "/v1/addresses/" id))
        payload         (merge (get-headers)
                               (auth-headers))
        resp            (delete-req url payload)]
    (:body resp)))

;--------------customers-----------------------------------------------------

(defn get-customers [& {:keys [fltr]}]
  (let [url             (url "/v1/customers")
        payload         (merge (get-headers) 
                               (if fltr {:query-params {:filter fltr}})
                               (auth-headers))
        resp            (get-req url payload)]
    (-> resp :body :customers)))


(defn add-customer [customer & the-rest]
  (let [url             (url "/v1/customers")
        address-fields  #{}
        payload         (merge (post-headers) 
                               (auth-headers) 
                               {:form-params customer})
        resp            (post-req url payload)] 
    (-> resp :body :customer)))

(defn update-customer [customer]
  (let [url             (url (str "/v1/customers/" (:id customer)))
        payload         (merge (post-headers)
                               (auth-headers)
                               {:form-params (dissoc customer :id)})
        resp            (update-req url payload)]
    (-> resp :body :customer)))

;-------------------------jobs----------------------------------------------

(defn get-jobs [& {:keys [fltr]}]
  (let [url             (url "/v1/jobs")
        payload         (merge (post-headers)
                               (if fltr {:query-params {:filter fltr}})
                               (auth-headers))
        resp            (get-req url payload)]
    (-> resp :body :jobs)))

(defn add-job [job & the-rest]
  (let [url             (url "/v1/jobs")
        payload         (merge (post-headers)
                               (auth-headers)
                               {:form-params job})
        resp            (post-req url payload)]
    (-> resp :body :job)))

;-----------------appointments---------------------------------------------

(defn get-appointments [& {:keys [fltr]}]
  (let [url             (url "/v1/appointments")
        payload         (merge (get-headers)
                               (if fltr {:query-params {:filter fltr}})
                               (auth-headers))
        resp            (get-req url payload)]
    (-> resp :body :appointments)))

(defn add-appointment [appt]
  (let [url             (url "/v1/appointments")
        payload         (merge (post-headers)
                               (auth-headers)
                               {:form-params appt})
        resp            (post-req url payload)]
    (-> resp :body :appointment)))

(defn update-appointment [appt]
  (let [url             (url (str "/v1/appointments/" (:id appt)))
        payload         (merge (post-headers)
                               (auth-headers)
                               (:form-params (dissoc appt :id)))
        resp            (update-req url payload)]
    (-> resp :body :appointment)))
