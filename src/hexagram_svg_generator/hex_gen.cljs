(ns hexagram-svg-generator.hex-gen
  (:require [shadow.resource]
            [clojure.edn :as edn]
            [clojure.string :as string]))

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

(def default-proportions
  (atom {:canvas  35
         :padding 2}))

(defn proportions
  [& {:keys [canvas padding gap-constant]
      :or   {canvas       33
             padding      0
             gap-constant 6.6}}]
  (let [width                      canvas
        height                     canvas
        hex-width                  (- canvas padding)
        hex-height                 (- canvas padding)
        line-height                (/ hex-height 11)
        vertical-gap-between-lines (* 2 line-height)
        yin-line-gap               (/ hex-width gap-constant)
        line-offset-x              (/ padding 2)
        yin-line-width             (- (/ hex-width 2)
                                      (/ yin-line-gap 2))
        line-offset-x-rightyin     (+ (/ padding 2) yin-line-width yin-line-gap)
        line-offset-y              (- (- width (/ padding 2)) line-height)]
    {:width                      width
     :height                     height
     :hex-width                  hex-width
     :hex-height                 hex-height
     :line-height                line-height
     :vertical-gap-between-lines vertical-gap-between-lines
     :yin-line-gap               yin-line-gap
     :yin-line-width             yin-line-width
     :line-offset-x              line-offset-x
     :line-offset-x-rightyin     line-offset-x-rightyin
     :line-offset-y              line-offset-y}))

(defn yang
  [line-number]
  ^{:key line-number}
  (let [{:keys [hex-width line-height
                line-offset-x
                line-offset-y]} (proportions)]
    [:rect
     {:id     (str "line-" (inc line-number) "-yang")
      :width  hex-width
      :height line-height
      :x      line-offset-x
      :y      line-offset-y}]))

(defn yin
  [line-number]
  ^{:key line-number}
  (let [{:keys [yin-line-width line-height
                line-offset-x line-offset-y
                line-offset-x-rightyin]} (proportions)]
    [:g
     [:rect {:id     (str "line-" (inc line-number) "-yin-left")
             :width  yin-line-width
             :height line-height
             :x      line-offset-x
             :y      line-offset-y}]
     [:rect {:id     (str "line-" (inc line-number) "-yin-right")
             :width  yin-line-width
             :height line-height
             :x      line-offset-x-rightyin
             :y      line-offset-y}]]))

(defn generate-svg
  [hexagram-number]
  (let [num   hexagram-number
        lines (for [n (range 6)]
                (nth (map int (seq (hexagram-bin num))) n))

        {:keys [width height
                vertical-gap-between-lines]} (proportions)]
    [:svg {:id                  (hexagram-id num)
           :viewBox             (string/join
                                  " "
                                  [0 0 width height])
           :preserveAspectRatio "xMidYMid meet"
           :version             "1.1"
           :xmlns               "http://www.w3.org/2000/svg"
           :class               "hexagram"
           :style               #js {:fill "currentColor"}}
     [:g {:id "g-hexagram"}
      (for [line-num (range 6)]
        ^{:key (str "g-line-" (inc line-num))}
        [:g {:id        (str "g-line-" (inc line-num))
             :transform (str "translate(0,"
                             (* line-num
                                (- vertical-gap-between-lines))
                             ")")}
         (condp = (nth lines line-num)
           0 (yin line-num)
           1 (yang line-num))])]]))
