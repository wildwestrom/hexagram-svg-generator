(ns hexagram-svg-generator.hex-gen
  (:require [shadow.resource]
            [clojure.edn :as edn]))

(def all-hexagrams
  (edn/read-string (shadow.resource/inline "./hexagrams.edn")))

(defn current-hexagram
  [num]
  (nth all-hexagrams (dec num)))

(defn hexagram-id
  [num]
  (str "iching-hexagram-" num))

(defn hexagram-bin [num]
  (:binary (current-hexagram num)))

(defn hexagram-name [num]
  (:name (current-hexagram num)))

(defn yang
  [n]
  ^{:key n}
  [:rect {:id (str "r-line" n "-yang")
          :width 66 :height 6
          :x 2 :y 62}])

(defn yin
  [n]
  ^{:key n}
  [:g
   [:rect {:id (str "r-line" n "yinleft")
           :width 28 :height 6
           :x 2 :y 62}]
   [:rect {:id (str "r-line" n "yinright")
           :width 28 :height 6
           :x 40 :y 62}]])

(defn hexagram-svg
  [hexagram-number]
  (let [num hexagram-number
        lines (for [n (range 6)]
                (nth (map int (seq (hexagram-bin num))) n))]
    [:div
     [:svg {:id (hexagram-id num)
            :width 70
            :height 70
            :class "hexagram"}
      [:g {:id "g-hexagram"}
       (for [n (range 6)]
         [:g {:id (str "g-line" (inc n))
              :transform (str "translate(0," (* n -12) ")")}
          (condp = (nth lines n)
            0 (yin n)
            1 (yang n))])]]]))
