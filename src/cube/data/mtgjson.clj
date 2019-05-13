(ns cube.data.mtgjson
  (:require [clojure.spec.alpha :as s]
            [jsonista.core :as json]
            [mtg.card :as card]))

(s/check-asserts true)

(defn all-cards-json []
  (json/read-value (slurp "dev/data/AllCards.json")
    (json/object-mapper {:decode-key-fn keyword})))

(def mtgjson->color
  {"B" :mtg.color/black
   "R" :mtg.color/red
   "G" :mtg.color/green
   "W" :mtg.color/white
   "U" :mtg.color/blue
   "C" :mtg.color/colorless})

(defn- mtgjson->color* [color-string]
  (assert (#{"B" "U" "R" "G" "W" "C"} color-string))
  (mtgjson->color color-string))

(defn- mtgjson->mana-cost [mana-cost-string]
  (let [mana-symbols (map second (re-seq #"\{(\w)\}" (or mana-cost-string "")))
        safe-inc     (fnil inc 0)]
    (reduce
      (fn [acc val]
        (let [color      (get mtgjson->color val)
              mana-color (if color
                           (keyword "mtg.mana.color" (name color))
                           :mtg.mana.color/any)]
          (update acc mana-color safe-inc)))
      {}
      mana-symbols)))


(defn card-to-remove?
  "Remove the insanity"
  [mtgjson-card]
  (let [is-whole-number? (fn [x]
                           (zero? (- x (int x))))]
    (or (not (is-whole-number? (:convertedManaCost mtgjson-card))))))

(defn all-cards-data
  ([] (all-cards-data (all-cards-json)))
  ([json-data]
   (reduce
     (fn [acc [_ card]]
       (if (card-to-remove? card)
         acc
         (let [card-name (:name card)
               card-data {::card/name                card-name
                          ::card/color-identity      (into #{} (map mtgjson->color*) (:colorIdentity card))
                          ::card/colors              (into #{} (map mtgjson->color*) (:colors card))
                          ::card/converted-mana-cost (int (:convertedManaCost card))
                          ::card/layout              (keyword "mtg.card.layout" (:layout card))
                          ::card/mana-cost           (mtgjson->mana-cost (:manaCost card))}]
           (s/assert :mtg/card card-data)
           (assoc acc card-name card-data))))
     {}
     json-data)))

(comment
  (def all-json (all-cards-json))

  (def all-data (all-cards-data all-json))
  (take 50 all-data)

  (map mtgjson->mana-cost
    (map :manaCost
      (take 100 (vals all-json))))


  (into #{} (map :power) (vals all-json)) ;; TODO

  (filter #(= (:power %) "99") (vals all-json))

  (get all-json (keyword "Spatial Contortion")) ;; Colorless
  (filter card-to-remove? (vals all-json))
  )

#_{:colorIdentity     ["B"],
   :colors            ["B"],
   :convertedManaCost 4.0,
   :foreignData       [{:flavorText "„Sie sind vielzählig, greifen in Schwärmen an und sind immer hungrig. Es wundert mich nicht, dass sie für uns Ratten wie Brüder sind.\"",
                        :language   "German",
                        :name       "Heuschreckengeizhals",
                        :text       "Die maximale Handkartenzahl aller Gegner ist um 2 reduziert.",
                        :type       "Kreatur - Ratte, Schamane"}
                       {:flavorText "\"Son numerosas, atacan en enjambres y siempre tienen hambre. No es de extrañar que las ratas tengamos tanta afinidad con ellas.\"",
                        :language   "Spanish",
                        :name       "Ávido de langostas",
                        :text       "El tamaño máximo de mano de cada oponente se reduce en dos.",
                        :type       "Criatura - Chamán rata"}
                       {:flavorText "« Elles sont nombreuses, elles attaquent en groupe et elles ont toujours faim. Il est normal que les rats se sentent liés à elles. »",
                        :language   "French",
                        :name       "Avare aux sauterelles",
                        :text       "La taille maximale de la main de chaque adversaire est réduite de deux.",
                        :type       "Créature : rat et shamane"}
                       {:flavorText "\"Sono tanti, attaccano in sciami e sono sempre affamati. Non c'è da stupirsi che vadano così d'accordo con noi ratti.\"",
                        :language   "Italian",
                        :name       "Locusta Arraffona",
                        :text       "Il numero massimo di carte che ogni avversario può tenere in mano è ridotto di due.",
                        :type       "Creatura - Sciamano Ratto"}
                       {:flavorText "その数膨大で、群れを成して襲い掛かり、常に飢えている。 我々鼠が親近感を覚えても不思議は無い。",
                        :language   "Japanese",
                        :name       "蝗たかりの守銭奴",
                        :text       "各対戦相手の手札の最大枚数は２減る。",
                        :type       "クリーチャー — ネズミ・シャーマン"}
                       {:flavorText "\"Eles são numerosos, atacam em grupos, e estão sempre famintos. Não é surpresa que nós ratos sentimos um grande parentesco com eles.\"",
                        :language   "Portuguese (Brazil)",
                        :name       "Gafanhoto Avaro",
                        :text       "O número máximo permitido de cards na mão de cada oponente é reduzido em dois.",
                        :type       "Criatura - Xamã Rato"}
                       {:flavorText "「他们数量庞大，成群出击，永远饥饿； 难怪会成为我们老鼠的好伙伴。」",
                        :language   "Chinese Simplified",
                        :name       "蝗虫守财奴",
                        :text       "每位对手的手牌上限减少两张。",
                        :type       "生物～老鼠／祭师"}],
   :layout            "normal",
   :legalities        {:commander "Legal",
                       :duel      "Legal",
                       :legacy    "Legal",
                       :modern    "Legal",
                       :vintage   "Legal"},
   :manaCost          "{2}{B}{B}",
   :mtgstocksId       4963,
   :name              "Locust Miser",
   :power             "2",
   :prices            {:paper {:2019-01-23 0.78,
                               :2019-01-30 0.77,
                               :2019-02-06 0.76,
                               :2019-02-13 0.76,
                               :2019-02-20 0.75,
                               :2019-02-27 0.75,
                               :2019-03-06 0.74,
                               :2019-03-13 0.76,
                               :2019-03-20 0.74,
                               :2019-03-27 0.76,
                               :2019-04-03 0.8,
                               :2019-04-10 0.77,
                               :2019-04-17 0.78,
                               :2019-04-24 0.81}},
   :printings         ["SOK"],
   :purchaseUrls      {:cardmarket "https://mtgjson.com/links/b20d8bd39a3efb3a",
                       :mtgstocks  "https://mtgjson.com/links/788f73bdac2b0193",
                       :tcgplayer  "https://mtgjson.com/links/253ed720e13b61f5"},
   :rulings           [{:date "2009-10-01",
                        :text "If multiple effects modify a player’s hand size, apply them in timestamp order. For example, if you put Cursed Rack (an artifact that sets a chosen player’s maximum hand size to four) onto the battlefield choosing your opponent and then put Locust Miser onto the battlefield, your opponent will have a maximum hand size of two. However, if those permanents entered the battlefield in the opposite order, your opponent’s maximum hand size would be four."}],
   :scryfallOracleId  "75a53a88-51a5-4c76-9f67-ee94c3065b98",
   :subtypes          ["Rat" "Shaman"],
   :supertypes        [],
   :text              "Each opponent's maximum hand size is reduced by two.",
   :toughness         "2",
   :type              "Creature — Rat Shaman",
   :types             ["Creature"],
   :uuid              "ba7940e7-84cb-5002-b1f1-cfaf8ef75656"}
