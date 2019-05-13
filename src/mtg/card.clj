(ns mtg.card
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]))

(def ^:private non-empty-string? (s/and string? not-empty))

(s/def ::name non-empty-string?)

(s/def :mtg/colors
  #{:mtg.color/black
    :mtg.color/red
    :mtg.color/green
    :mtg.color/white
    :mtg.color/blue
    :mtg.color/colorless})

(s/def ::color-identity (s/coll-of :mtg/colors :kind set?))
(s/def ::colors (s/coll-of :mtg/colors :kind set?))

(s/def ::converted-mana-cost
  (s/spec nat-int? :gen #(s/gen (s/int-in 0 12))))

(s/def ::layout
  #{:mtg.card.layout/aftermath
    :mtg.card.layout/augment
    :mtg.card.layout/emblem
    :mtg.card.layout/flip
    :mtg.card.layout/host
    :mtg.card.layout/leveler
    :mtg.card.layout/meld
    :mtg.card.layout/normal
    :mtg.card.layout/planar
    :mtg.card.layout/saga
    :mtg.card.layout/scheme
    :mtg.card.layout/split
    :mtg.card.layout/transform
    :mtg.card.layout/vanguard})

(s/def :mtg.mana/colors
  #{:mtg.mana.color/black
    :mtg.mana.color/red
    :mtg.mana.color/green
    :mtg.mana.color/white
    :mtg.mana.color/blue
    :mtg.mana.color/colorless
    :mtg.mana.color/any})

(s/def ::mana-cost
  (s/map-of
    :mtg.mana/colors
    (s/spec pos-int? :gen #(s/gen (s/int-in 1 4)))))

#_(def last-spec *1)
#_(gen/generate (s/gen last-spec))

(s/def :mtg/card
  (s/keys :req [::name
                ::color-identity
                ::colors
                ::converted-mana-cost
                ::layout
                ::mana-cost]))
