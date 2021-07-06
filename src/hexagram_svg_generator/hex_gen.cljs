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

(def proportions
  (let [canvas                     33
        min-x                      0
        min-y                      0
        width                      canvas
        height                     canvas
        line-height                (/ height 11)
        vertical-gap-between-lines (* 2 line-height)
        yin-line-gap               (/ width 6.6)
        line-offset-x              min-x
        yin-line-width             (- (/ width 2)
                                      (/ yin-line-gap 2))
        line-offset-x-rightyin     (+ yin-line-width yin-line-gap)
        line-offset-y              (- width line-height)]
    {:min-x                      min-x
     :min-y                      min-y
     :width                      width
     :height                     height
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
  [:rect
   {:id     (str "line-" (inc line-number) "-yang")
    :width  (:width proportions)
    :height (:line-height proportions)
    :x      (:line-offset-x proportions)
    :y      (:line-offset-y proportions)}])

(defn yin
  [line-number]
  ^{:key line-number}
  [:g
   [:rect {:id     (str "line-" (inc line-number) "-yin-left")
           :width  (:yin-line-width proportions)
           :height (:line-height proportions)
           :x      (:line-offset-x proportions)
           :y      (:line-offset-y proportions)}]
   [:rect {:id     (str "line-" (inc line-number) "-yin-right")
           :width  (:yin-line-width proportions)
           :height (:line-height proportions)
           :x      (:line-offset-x-rightyin proportions)
           :y      (:line-offset-y proportions)}]])

(defn hexagram-svg
  [hexagram-number]
  (let [num   hexagram-number
        lines (for [n (range 6)]
                (nth (map int (seq (hexagram-bin num))) n))]
    [:svg {:id                  (hexagram-id num)
           :viewBox             (string/join
                                  " "
                                  [(:min-x proportions)
                                   (:min-y proportions)
                                   (:width proportions)
                                   (:height proportions)])
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
                                (- (:vertical-gap-between-lines proportions)))
                             ")")}
         (condp = (nth lines line-num)
           0 (yin line-num)
           1 (yang line-num))])]]))
