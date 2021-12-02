package org.ergoplatform.dex.index.db

import io.circe.Json
import io.circe.syntax._
import org.ergoplatform.dex.domain.amm.OrderEvaluation.{DepositEvaluation, RedeemEvaluation, SwapEvaluation}
import org.ergoplatform.dex.domain.amm.{Deposit, EvaluatedCFMMOrder, OrderId, PoolId, PoolStateId, Redeem, Swap}
import org.ergoplatform.ergo.models.Output
import org.ergoplatform.ergo._

object models {

  final case class DBSwap(
    orderId: OrderId,
    poolId: PoolId,
    poolStateId: Option[PoolStateId],
    maxMinerFee: Long,
    timestamp: Long,
    inputId: TokenId,
    inputValue: Long,
    inputTicker: Option[String],
    minOutputId: TokenId,
    minOutputAmount: Long,
    minOutputTicker: Option[String],
    outputAmount: Option[Long],
    dexFeePerTokenNum: Long,
    dexFeePerTokenDenom: Long,
    p2pk: Address
  )

  implicit val swapView: DBView[EvaluatedCFMMOrder[Swap, SwapEvaluation], DBSwap] = {
    case EvaluatedCFMMOrder(swap, ev, pool) =>
      DBSwap(
        OrderId.fromBoxId(swap.box.boxId),
        swap.poolId,
        pool.map(p => PoolStateId(p.box.boxId)),
        swap.maxMinerFee,
        swap.timestamp,
        swap.params.input.id,
        swap.params.input.value,
        swap.params.input.ticker,
        swap.params.minOutput.id,
        swap.params.minOutput.value,
        swap.params.minOutput.ticker,
        ev.map(_.output.value),
        swap.params.dexFeePerTokenNum,
        swap.params.dexFeePerTokenDenom,
        swap.params.p2pk
      )
  }

  final case class DBRedeem(
    orderId: OrderId,
    poolId: PoolId,
    poolStateId: Option[PoolStateId],
    maxMinerFee: Long,
    timestamp: Long,
    lpId: TokenId,
    lpAmount: Long,
    lpTicker: Option[String],
    outputAmountX: Option[Long],
    outputAmountY: Option[Long],
    dexFee: Long,
    p2pk: Address
  )

  implicit val redeemView: DBView[EvaluatedCFMMOrder[Redeem, RedeemEvaluation], DBRedeem] = {
    case EvaluatedCFMMOrder(redeem, ev, pool) =>
      DBRedeem(
        OrderId.fromBoxId(redeem.box.boxId),
        redeem.poolId,
        pool.map(p => PoolStateId(p.box.boxId)),
        redeem.maxMinerFee,
        redeem.timestamp,
        redeem.params.lp.id,
        redeem.params.lp.value,
        redeem.params.lp.ticker,
        ev.map(_.outputX.value),
        ev.map(_.outputY.value),
        redeem.params.dexFee,
        redeem.params.p2pk
      )
  }

  final case class DBDeposit(
    orderId: OrderId,
    poolId: PoolId,
    poolStateId: Option[PoolStateId],
    maxMinerFee: Long,
    timestamp: Long,
    inputIdX: TokenId,
    inputAmountX: Long,
    inputTickerX: Option[String],
    inputIdY: TokenId,
    inputAmountY: Long,
    inputTickerY: Option[String],
    outputAmountLP: Option[Long],
    dexFee: Long,
    p2pk: Address
  )

  implicit val depositView: DBView[EvaluatedCFMMOrder[Deposit, DepositEvaluation], DBDeposit] = {
    case EvaluatedCFMMOrder(deposit, ev, pool) =>
      DBDeposit(
        OrderId.fromBoxId(deposit.box.boxId),
        deposit.poolId,
        pool.map(p => PoolStateId(p.box.boxId)),
        deposit.maxMinerFee,
        deposit.timestamp,
        deposit.params.inX.id,
        deposit.params.inX.value,
        deposit.params.inX.ticker,
        deposit.params.inY.id,
        deposit.params.inY.value,
        deposit.params.inY.ticker,
        ev.map(_.outputLP.value),
        deposit.params.dexFee,
        deposit.params.p2pk
      )
  }

  final case class DBOutput(
    boxId: BoxId,
    transactionId: TxId,
    value: Long,
    index: Int,
    globalIndex: Long,
    creationHeight: Int,
    settlementHeight: Int,
    ergoTree: SErgoTree,
    address: Address,
    additionalRegisters: Json
  )

  implicit val outputView: DBView[Output, DBOutput] =
    (output: Output) =>
      DBOutput(
        output.boxId,
        output.transactionId,
        output.value,
        output.index,
        output.globalIndex,
        output.creationHeight,
        output.settlementHeight,
        output.ergoTree,
        output.address,
        output.additionalRegisters.asJson
      )
}
