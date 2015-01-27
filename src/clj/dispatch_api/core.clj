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
(def route-maps 
  {:users             {:route "/v1/users" :singular :user :plural :users}
   :organizations     {:route "/v1/organizations" :singular :organization :plural :organizations}
   :appointments      {:route "/v1/appointments" :singular :appointment :plural :appointments}
   :attachments       {:route "/v1/attachments" :singular :attachment :plural :attachments}
   :addresses         {:route "/v1/addresses" :singular :address :plural :addresses}
   :jobs              {:route "/v1/jobs" :singular :job :plural :jobs}
   :customers         {:route "/v1/customers" :singular :customer :plural :customers}})

(defn get-things [entity fltr]
  (let [url             (url (:route entity))
        my-key          (:plural entity)
        payload         (merge (get-headers)
                               (when fltr {:query-params {:filter fltr}})
                               (auth-headers))
        resp            (get-req url payload)]
    (-> resp :body my-key)))

(defn add-thing [entity thing]
  (let [url             (url (:route entity))
        my-key          (:singular entity)
        payload         (merge (post-headers)
                               (auth-headers)
                               {:form-params thing})
        resp            (post-req url payload)]
    (-> resp :body my-key)))
(defn update-thing [entity thing]
  (let [url             (url (str (:route entity) "/" (:id thing)))
        my-key          (:singular entity)
        payload         (merge (get-headers)
                               (auth-headers)
                               {:form-params (dissoc thing :id)})
        resp            (update-req url payload)]
    (-> resp :body my-key)))

(defn delete-thing [entity id]
  (let [url             (url (str (:route entity) "/" id))
        payload         (merge (post-headers)
                               (auth-headers))
        resp            (delete-req url payload)]
    (:body resp)))
;----------------------users-----------------------------------------------

(defn whoami []
  (let [entity          (assoc (:users route-maps)
                               :route "/v1/me"
                               :plural :user)]
    (get-things entity nil)))

(defn get-users [& {:keys [fltr]}]
  (get-things (:users route-maps) fltr))

(defn delete-user [id]
  (delete-thing (:users route-maps) id))

(defn get-technicians [& {:keys [fltr]}]
  (let [roles           {:by_user_roles "technician"}]
    (get-users :fltr (merge roles fltr))))


(defn get-dispatchers [& {:keys [fltr]}]
  (let [roles           {:by_user_roles "dispatcher"}]
    (get-users :fltr roles)))

;---------------------------organizations----------------------------------

(defn get-organizations [& {:keys [fltr]}]
  (get-things (:organizations route-maps) fltr))

;--------------addresses-----------------------------------------------------

(defn get-addresses [& {:keys [fltr]}]
  (get-things (:addresses route-maps) fltr))

(defn add-address [address]
  (add-thing (:addresses route-maps) address))

(defn update-address [address]
  (update-thing (:addresses route-maps) address))

(defn delete-address [id]
  (delete-thing (:addresses route-maps) id))

;--------------customers-----------------------------------------------------

(defn get-customers [& {:keys [fltr]}]
  (get-things (:customers route-maps) fltr))


(defn add-customer [customer]
  (add-thing (:customers route-maps) customer))

(defn update-customer [customer]
  (update-thing (:customers route-maps) customer))

;-------------------------jobs----------------------------------------------

(defn get-jobs [& {:keys [fltr]}]
  (get-things (:jobs route-maps) fltr))

(defn add-job [job]
  (add-thing (:jobs route-maps) job))

;-----------------appointments---------------------------------------------

(defn get-appointments [& {:keys [fltr]}]
  (get-things (:appointments route-maps) fltr))

(defn add-appointment [appt]
  (add-thing (:appointments route-maps) appt))

(defn update-appointment [appt]
  (update-thing (:appointments route-maps) appt))

;--------------------attachments-------------------------------------------

(defn get-attachments [& {:keys [fltr]}]
  (get-things (:attachments route-maps) fltr))

(defn add-attachment [attach]
  (add-thing (:attachments route-maps) attach))

(defn update-attachment [attach]
  (update-thing (:attachments route-maps) attach))

(defn delete-attachment [id]
  (delete-thing (:attachments route-maps) id))
