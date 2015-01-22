(ns dispatch-api.config
  (:require 
    [clj-http.client :as client]
    [slingshot.slingshot :refer [try+]]))


;-----------------------------declarations--------------------------------
(defonce token (atom nil))
(defonce refresh (atom nil))

(def client-id        (System/getenv "DISPATCH_ID"))
(def client-secret    (System/getenv "DISPATCH_SECRET"))
(def user-name        (System/getenv "DISPATCH_USER"))
(def password         (System/getenv "DISPATCH_PASS"))

(def base-url  "https://api-sandbox.dispatch.me")
(def ^:dynamic *debug*      false)

(def get-req      client/get)
(def post-req     client/post)
(def update-req   client/patch)
(def delete-req   client/delete)


;--------------------utility functions---------------------------------------
(def url #(str base-url %))

(def base-headers #(assoc {} :accept :application/json
                             :debug? *debug*
                             :debug-body *debug*
                             :as :json))

(def get-headers  
  #(merge (base-headers) {:content-type :application/json}))

(def post-headers 
  #(merge (base-headers) {:content-type :x-www-form-urlencoded}))

(def auth-headers 
  #(assoc {} :headers { "Authorization" (str "Bearer " @token)}))

;-------------------------authentication------------------------------------
(defn get-token
  ([] 
    (let [params        (assoc {} :client client-id
                                  :secret client-secret)]
      (get-token params))) 
  ([{:keys [user pass client secret]
      :or {client client-id 
            secret client-secret}
      :as creds}]
  (let [url           (url "/oauth/token")
        form          (assoc {} :client_secret secret
                                :client_id client)
        user-cred     (if 
                        (and user pass) 
                        (assoc {} :username user :password pass))
        grant         (if
                        (and user pass)
                        (assoc {} :grant_type "password")
                        (assoc {} :grant_type "client_credentials"))
        form          (merge form user-cred grant)
        payload       (merge (post-headers) {:form-params form})
        resp          (post-req url payload)]
    (reset! refresh   (-> resp :body :refresh_token))
    (reset! token     (-> resp :body :access_token)) 
    (:body resp))))

(defn refresh-token 
  ([]
   (let [params       (assoc {} :client client-id
                                :secret client-secret)]
     (refresh-token params)))
  ([{:keys [user pass client secret]
      :or  {client client-id 
            secret client-secret}
      :as creds}]
  (if (nil? @token)
    (get-token creds)
    (try+
      (let [url         (url "/oauth/token")
            form        (assoc {} :grant_type "refresh_token"
                                  :client_id client
                                  :client_secret secret
                                  :refresh_token @refresh)
            payload     (merge (post-headers) {:form-params form})
            resp        (post-req url payload)]
        (reset! refresh (-> resp :body :refresh_token))
        (reset! token     (-> resp :body :access_token)) 
        (:body resp))
      (catch [:status 401] _ (get-token creds))))))

(defmacro with-auth
  [opts & body]
  `(do
     (refresh-token ~opts)
     ~@body))

(defmacro with-debug
  [& body]
  `(binding [*debug* true]  ~@body))
