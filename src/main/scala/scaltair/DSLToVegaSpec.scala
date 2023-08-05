package scaltair

import scaltair.vegalite.VegaLiteDSL
import scaltair.Data.ColumnData
import scaltair.vegalite.NonArgAggregateOp
import scaltair.vegalite.EdEncoding
import dotty.tools.dotc.semanticdb.SymbolInformation.Property.DEFAULT

object DSLToVegaSpec:

  final val DEFAULT_WIDTH = 600
  final val DEFAULT_HEIGHT = 400

  import Chart.*

  def createVegeLiteSpec(chart: CompleteChart): VegaLiteDSL =
    chart match
      case ChartWithSingleView(data, view) =>
        val encoding = createEncoding(view.channels)
        VegaLiteDSL(
          data = Some(columnDataToVegaLiteData(data)),
          encoding = Some(encoding),
          mark = Some(markToVegaMark(view.mark, view.clip, view.opacity)),
          width = Some(DEFAULT_WIDTH),
          height = Some(DEFAULT_HEIGHT)
        )

      case ChartWithLayeredView(data, layeredView) =>
        VegaLiteDSL(
          data = Some(columnDataToVegaLiteData(data)),
          layer = Some(
            layeredView.views
              .map(view =>
                vegalite
                  .LayerSpec(
                    encoding = Some(
                      edEncodingToLayerEncoding(createEncoding(view.channels))
                    ),
                    mark =
                      Some(markToVegaMark(view.mark, view.clip, view.opacity))
                  )
              )
          ),
          width = Some(DEFAULT_WIDTH),
          height = Some(DEFAULT_HEIGHT)
        )

      case HConcatChart(leftChart, rightChart) =>
        val leftVegaLiteSpec = vegaLiteDSLToSpec(createVegeLiteSpec(leftChart))
        val rightVegaLiteSpec = vegaLiteDSLToSpec(
          createVegeLiteSpec(rightChart)
        )
        VegaLiteDSL(
          hconcat = Some(Seq(leftVegaLiteSpec, rightVegaLiteSpec))
        )

      case VConcatChart(upperChart, lowerChart) =>
        val upperVegaLiteSpec = vegaLiteDSLToSpec(
          createVegeLiteSpec(upperChart)
        )
        val lowerVegaLiteSpec = vegaLiteDSLToSpec(
          createVegeLiteSpec(lowerChart)
        )
        VegaLiteDSL(
          vconcat = Some(Seq(upperVegaLiteSpec, lowerVegaLiteSpec))
        )

      case CompleteChartWithProperties(chart, properties) =>
        val vegaLiteDSL = createVegeLiteSpec(chart)
        vegaLiteDSL.copy(
          width = Some(properties.width),
          height = Some(properties.height),
          title = Some(properties.title)
        )

  private def createEncoding(channels: Seq[Channel]): EdEncoding =
    import Channel.*

    channels.foldLeft(vegalite.EdEncoding())((encoding, channel) =>
      channel match
        case XChannel(fieldName, fieldType, bins, scale, axis) =>
          EdEncoding(
            x = Some(
              vegalite.XClass(
                field = Some(fieldName),
                `type` = Some(fieldTypeToVegaFieldType(fieldType)),
                bin = binToVegalinBin(bins),
                scale = scaleToVegaliteScale(scale),
                axis = axisToVegaliteAxis(axis)
              )
            )
          )
        case YChannel(fieldName, fieldType, aggregateType, scale, axis) =>
          encoding.copy(y =
            Some(
              vegalite.YClass(
                field = Some(fieldName),
                `type` = Some(fieldTypeToVegaFieldType(fieldType)),
                aggregate = aggregateToVegaliteAggregate(aggregateType),
                scale = scaleToVegaliteScale(scale),
                axis = axisToVegaliteAxis(axis)
              )
            )
          )
        case Y2Channel(fieldName, fieldType, aggregateType) =>
          encoding.copy(y2 =
            Some(
              vegalite.Y2Class(
                field = Some(fieldName),
                `type` = Some(fieldTypeToVegaFieldType(fieldType)),
                aggregate = aggregateToVegaliteAggregate(aggregateType)
              )
            )
          )
        case ColorChannel(fieldName, fieldType) =>
          encoding.copy(color =
            Some(
              vegalite.ColorClass(
                field = Some(fieldName),
                `type` = Some(fieldTypeToVegaFieldType(fieldType))
              )
            )
          )
        case SizeChannel(fieldName) =>
          encoding.copy(size =
            Some(
              vegalite.SizeClass(
                field = Some(fieldName)
              )
            )
          )
        case TextChannel(fieldName) =>
          encoding.copy(text =
            Some(
              vegalite.TextDef(
                field = Some(fieldName)
              )
            )
          )
    )

  // the VegaLiteDSL contains almost the same fields as the vegalite.Spec. To avoid
  // code duplication, we convert the VegaLiteDSL to vegalite.Spec
  private def vegaLiteDSLToSpec(dsl: VegaLiteDSL): vegalite.Spec =
    println("width is " + dsl.width)
    vegalite.Spec(
      data = dsl.data,
      mark = dsl.mark,
      encoding = dsl.encoding,
      width = dsl.width,
      height = dsl.height,
      title = dsl.title,
      hconcat = dsl.hconcat,
      vconcat = dsl.vconcat,
      layer = dsl.layer
    )

  // the EdEncoding contain almost the same fields as the LayerEncoding. To avoid
  // code duplication, we convert the EdEncoding to LayerEncoding
  private def edEncodingToLayerEncoding(
      encoding: EdEncoding
  ): vegalite.LayerEncoding =
    vegalite.LayerEncoding(
      x = encoding.x,
      y = encoding.y,
      y2 = encoding.y2,
      color = encoding.color,
      size = encoding.size,
      text = encoding.text
    )

  private def columnDataToVegaLiteData(data: ColumnData): vegalite.URLData =
    val values: vegalite.InlineDataset =
      // all columns need to have the same number of values
      val columns = data.keys.toSeq.map(key => data(key))
      require(columns.forall(_.size == columns.head.size))
      val numRows = columns.head.size
      (0 until numRows)
        .map(rowNum =>
          data.keys.toSeq.map(key => key -> Some(data(key)(rowNum))).toMap
        )
        .toSeq
    vegalite.URLData(values = Some(values))

  private def fieldTypeToVegaFieldType(fieldType: FieldType): vegalite.Type =
    fieldType match
      case FieldType.Quantitative => vegalite.Type.quantitative
      case FieldType.Ordinal      => vegalite.Type.ordinal
      case FieldType.Nominal      => vegalite.Type.nominal
      case FieldType.Temporal     => vegalite.Type.temporal

  private def markToVegaMark(
      mark: Mark,
      clipMark: Boolean,
      opacity: Double
  ): vegalite.Def =
    mark match
      case Mark(MarkType.Line, clipMark, opacity) =>
        vegalite.Def(
          `type` = "line",
          clip = Some(clipMark),
          opacity = Some(opacity)
        )
      case Mark(MarkType.Circle, clipMark, opacity) =>
        vegalite.Def(
          `type` = "circle",
          clip = Some(clipMark),
          opacity = Some(opacity)
        )
      case Mark(MarkType.Rect, clipMark, opacity) =>
        vegalite.Def(
          `type` = "rect",
          clip = Some(clipMark),
          opacity = Some(opacity)
        )
      case Mark(MarkType.Point, clipMark, opacity) =>
        vegalite.Def(
          `type` = "point",
          clip = Some(clipMark),
          opacity = Some(opacity)
        )
      case Mark(MarkType.Bar, clipMark, opacity) =>
        vegalite.Def(
          `type` = "bar",
          clip = Some(clipMark),
          opacity = Some(opacity)
        )
      case Mark(MarkType.Area, clipMark, opacity) =>
        vegalite.Def(
          `type` = "area",
          clip = Some(clipMark),
          opacity = Some(opacity)
        )
      case Mark(MarkType.Boxplot, clipMark, opacity) =>
        vegalite.Def(`type` = "boxplot", clip = Some(clipMark))
      case Mark(MarkType.Errorband, clipMark, opacity) =>
        vegalite.Def(`type` = "errorband", clip = Some(clipMark))
      case Mark(MarkType.Text, clipMark, opacity) =>
        vegalite.Def(`type` = "text", clip = Some(clipMark))

  private def binToVegalinBin(
      bin: Option[Bin]
  ): Option[vegalite.BinParams] | Option[Boolean] =
    bin match
      case Some(Bin.Auto) => Some(true)
      case Some(Bin.CustomBinning(properties)) =>
        properties.maxbins.map(numBins =>
          vegalite.BinParams(maxbins = Some(numBins))
        )
      case None => None

  private def aggregateToVegaliteAggregate(
      aggregate: Option[AggregateType]
  ): Option[vegalite.NonArgAggregateOp] =
    aggregate match
      case Some(AggregateType.Count) => Some(vegalite.NonArgAggregateOp.count)
      case Some(AggregateType.Sum)   => Some(vegalite.NonArgAggregateOp.sum)
      case Some(AggregateType.Mean)  => Some(vegalite.NonArgAggregateOp.mean)
      case None                      => None
      case Some(agg) => throw Exception(s"Aggregate type $agg unexpected")

  private def scaleToVegaliteScale(
      scale: Option[Scale]
  ): Option[vegalite.Scale] =
    scale match
      case Some(Scale(includeZero, scaleTypeOpt, domainOpt)) =>
        val vlScaleWithZero = vegalite.Scale(zero = Some(includeZero))
        val vlScaleWithScaleType = scaleTypeOpt match
          case Some(scaleType) =>
            vlScaleWithZero.copy(`type` =
              Some(scaleTypeToVegaliteScaleType((scaleType)))
            )
          case None => vlScaleWithZero
        val vlScaleWithScaleTypeAndDomain = domainOpt match
          case Some(domain) =>
            vlScaleWithScaleType.copy(
              domainMin = Some(domain.min),
              domainMax = Some(domain.max)
            )
          case None => vlScaleWithScaleType
        Some(vlScaleWithScaleTypeAndDomain)
      case None => None

  private def scaleTypeToVegaliteScaleType(
      scaleType: ScaleType
  ): vegalite.ScaleType =
    scaleType match
      case ScaleType.Logarithmic => vegalite.ScaleType.log
      case ScaleType.Linear      => vegalite.ScaleType.linear

  private def axisToVegaliteAxis(axis: Option[Axis]): Option[vegalite.Axis] =
    axis match
      case Some(labelFontSize, titleFontSize) =>
        Some(
          vegalite.Axis(
            labelFontSize = Some(labelFontSize),
            titleFontSize = Some(titleFontSize)
          )
        )
      case None => None
