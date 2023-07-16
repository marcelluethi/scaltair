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
          mark = Some(markTypeToVegaMark(view.mark)),
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
                    mark = Some(markTypeToVegaMark(view.mark))
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
        case X(fieldName, fieldType, bins, scale, axis) =>
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
        case Y(fieldName, fieldType, aggregateType, scale, axis) =>
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
        case Y2(fieldName, fieldType, aggregateType) =>
          encoding.copy(y2 =
            Some(
              vegalite.Y2Class(
                field = Some(fieldName),
                `type` = Some(fieldTypeToVegaFieldType(fieldType)),
                aggregate = aggregateToVegaliteAggregate(aggregateType)
              )
            )
          )
        case Color(fieldName, fieldType) =>
          encoding.copy(color =
            Some(
              vegalite.ColorClass(
                field = Some(fieldName),
                `type` = Some(fieldTypeToVegaFieldType(fieldType))
              )
            )
          )
        case Size(fieldName) =>
          encoding.copy(size =
            Some(
              vegalite.SizeClass(
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
      size = encoding.size
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

  private def markTypeToVegaMark(markType: MarkType): String =
    markType match
      case MarkType.Line      => "line"
      case MarkType.Circle    => "circle"
      case MarkType.Rect      => "rect"
      case MarkType.Point     => "point"
      case MarkType.Bar       => "bar"
      case MarkType.Area      => "area"
      case MarkType.Boxplot   => "boxplot"
      case MarkType.ErrorBand => "errorband"

  private def binToVegalinBin(
      bin: Option[Bin]
  ): Option[vegalite.BinParams] | Option[Boolean] =
    bin match
      case Some(Bin.Auto)       => Some(true)
      case Some(Bin.MaxBins(n)) => Some(vegalite.BinParams(maxbins = Some(n)))
      case None                 => None

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
      case Some(includeZero, None) =>
        Some(vegalite.Scale(zero = Some(includeZero)))
      case Some(_, Some(range)) =>
        Some(
          vegalite.Scale(
            domainMin = Some(range.start),
            domainMax = Some(range.end)
          )
        )
      case None => None

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
