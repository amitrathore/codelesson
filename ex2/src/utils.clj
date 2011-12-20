(ns utils
  (:use clojure.pprint)
  (:import [java.text SimpleDateFormat]
           [java.util Date Calendar TimeZone GregorianCalendar]
           [org.joda.time DateTime Days]))

(defn print-vals [& args]
  (apply println (cons "*** " (map #(if (string? %) % (with-out-str (pprint %)))  args)))
  (last args))

(defn days-between [date-a date-b]
  (.getDays (Days/daysBetween (DateTime. date-a) (DateTime. date-b))))


(defn time-zone [tz-string]
  (TimeZone/getTimeZone tz-string))

(defn sdf [sdf-pattern tz-string]
  (doto (SimpleDateFormat. sdf-pattern)
    (.setTimeZone (time-zone tz-string))))

(defn date-from-str [sdf-pattern string]
  (.parse (sdf sdf-pattern "UTC") string))

