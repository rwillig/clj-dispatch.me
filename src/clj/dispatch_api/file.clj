(ns dispatch-api.file
  (:require 
    [clj-http.client      :as client]
    [dispatch-api.config  :refer [get-req 
                                  post-req
                                  update-req
                                  delete-req
                                  base-headers
                                  auth-headers
                                  get-headers
                                  post-headers]
                          :as conf]
    [clojure.java.io      :as io]
    [clojure.string       :refer [split]]
    [slingshot.slingshot  :refer [try+]]))


(def base-url   "https://files-api-sandbox.dispatch.me")
(def url #(str base-url %))

;--------------------file utilities----------------------------------------

(defn read-file  [file-path]
    (with-open  [reader  (io/input-stream file-path)]
      (println (type reader))
          (let  [length  (.length  (io/file file-path))
                           buffer  (byte-array length)]
                  (.read reader buffer 0 length)
                        buffer)))
;---------------------files-------------------------------------------------

(defn get-file-info [uid]
  (let [url             (url (str "/v1/datafiles/" uid ".json"))
        payload         (get-headers)
        resp            (get-req url payload)]
    (-> resp :body :datafile)))

(defn download-file [uid format]
  (let [url             (url (str "/v1/datafiles/" uid "/" format))
        payload         (merge (get-headers) {:as :byte-array})
        resp            (get-req url payload)]
    (:body resp)
    resp))

(defn upload-file [filename mime-type caption]
  (let [url             (url "/v1/datafiles")
        multi           [
                         {:name "name" :content caption}
                         {:name "Content/type" :content mime-type}
                         {:name "filename" :content (last (split filename #"/"))}
                         {:name "file" :content (io/file filename) }]
        payload         (merge (base-headers)
                               (auth-headers)
                               {:multipart multi})
        resp            (post-req url payload)]
    (-> resp :body :datafile)))
