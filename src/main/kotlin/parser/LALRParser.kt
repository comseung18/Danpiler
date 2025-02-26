package parser

fun LR1ItemCollection.toLR0ItemCollection(): LR0ItemCollection {
    return LR0ItemCollection(
        this.items.map{ it.toLR0Item() }.toSet()
    )
}

fun LR1Item.toLR0Item() : LR0Item {
    return LR0Item(
        this.nonTerminal,
        this.production,
        this.dotIndex
    )
}

class LALRParser(grammar: Grammar, root: NonTerminalItem) : LR1Parser(grammar, root) {

    init {
        val lR1CollectionGroup =
            reverseLr1CollectionMap.keys.groupBy { it.toLR0ItemCollection() }

        var startCollectionCounterNumber = collectionCounter

        lR1CollectionGroup.forEach { (_, collections) ->
            val m = mutableMapOf<LR0Item, MutableSet<TerminalItem>>()
            collections.forEach {
                it.items.forEach { lr1Item ->
                    m.computeIfAbsent(lr1Item.toLR0Item()) {
                        mutableSetOf()
                    }.addAll(lr1Item.lookAhead)
                }
            }

            val newLR1Collection = LR1ItemCollection(
                m.map { (k, v) -> LR1Item(k.nonTerminal, k.production, k.dotIndex, v) }.toSet()
            )

            reverseLr1CollectionMap[newLR1Collection] = startCollectionCounterNumber
            lr1CollectionMap[startCollectionCounterNumber] = newLR1Collection

            collections.forEach { collection ->
                val index = reverseLr1CollectionMap[collection]
                if(index == 0) stackStartInt = startCollectionCounterNumber

                val newGoto = mutableMapOf<Pair<Int,GrammarItem>, Int>()
                goto.forEach { (t, j) ->
                    val (i, gItem) = t

                    // (i, terminal) -> j

                    val newI = if(i == index) startCollectionCounterNumber else i
                    val newJ = if(j == index) startCollectionCounterNumber else j

                    newGoto[newI to gItem] = newJ
                }

                goto.putAll(newGoto)
            }

            startCollectionCounterNumber++

            collections.forEach { collection ->
                val index = reverseLr1CollectionMap[collection]
                reverseLr1CollectionMap.remove(collection)
                lr1CollectionMap.remove(index)

                val removeKeys = goto.keys.filter { it.first == index || goto[it] == index }
                removeKeys.forEach { goto.remove(it) }
            }
        }
    }
}
