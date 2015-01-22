(set-env!
  :resource-paths #{"src/clj"}
  :wagons       '[[s3-wagon-private  "1.1.2"]]
  :repositories #(conj % ["private"  {:url  "s3p://rsw-hdfs/releases/"
                                            :username (System/getenv "AWS_KEY") 
                                            :passphrase (System/getenv "AWS_PASS")}])
  :dependencies '[[org.clojure/clojure "1.6.0"]
                  [org.clojure/data.json  "0.2.5"]
                  [cheshire  "5.4.0"]
                  [clj-time "0.9.0"]
                  [slingshot  "0.12.1"]
                  [clj-http "1.0.1"]])


(task-options!
  pom {:project 'dispatch
       :version "0.1.0"
       :description "sandbox for exercising dispatch.me API"})

