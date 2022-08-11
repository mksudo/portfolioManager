import React, {useContext, useEffect, useRef, useState} from "react";
import {IComponentStock} from "../model/componentStock";
import {requestAnalyzedPortfolio, requestComponentStock} from "../util/serverRequest";
import {
    Button,
    Form,
    FormInstance,
    Input,
    InputRef,
    Popconfirm,
    Table,
    DatePicker,
    InputNumber,
    Radio,
    RadioChangeEvent
} from "antd";
import {IPortfolio, IPortfolioAnalyzeResult} from "../model/portfolio";
import type {DatePickerProps, RangePickerProps} from "antd/es/date-picker";
import {ApexOptions} from "apexcharts";
import ReactApexChart from "react-apexcharts";

type IComponentStockAlloc = IComponentStock & {investment: number}
const EditableContext = React.createContext<FormInstance<any> | null>( null )
interface EditableRowProps { index: number }
interface EditableCellProps {
    title: React.ReactNode
    children: React.ReactNode
    dataIndex: keyof IComponentStockAlloc
    record: IComponentStockAlloc
    editable: boolean
    isNumber: boolean
    handleSave: (componentStock: IComponentStockAlloc) => void
}


const EditableRow: React.FC<EditableRowProps> = ({index, ...props}) => {
    const [form] = Form.useForm()
    return (
        <Form form={form} component={false}>
            <EditableContext.Provider value={form}>
                <tr {...props}/>
            </EditableContext.Provider>
        </Form>
    )
}

const EditableCell: React.FC<EditableCellProps> = ({
    title,
    children,
    dataIndex,
    record,
    editable,
    isNumber,
    handleSave,
    ...rest
}) => {
    const [editing, setEditing] = useState(false)
    const inputRef = useRef<InputRef>(null)
    const form = useContext(EditableContext)!

    useEffect(() => {
        if (editing) inputRef.current!.focus()
    }, [editing])

    const toggleEdit = () => {
        setEditing(!editing)
        form.setFieldsValue({ [dataIndex]: record[dataIndex] })
    }

    const save = async () => {
        try {
            const values = await form.validateFields()
            toggleEdit()
            handleSave({ ...record, ...values })
        } catch (err) {
            console.log('Save failed: ', err)
        }
    }

    let childNode = children

    if (editable) {
        childNode = editing ? isNumber ? (
            <Form.Item
                style={{ margin: 0 }}
                name={dataIndex}
                rules={[
                    {
                        required: true,
                        message: `${title} is required.`,
                    },
                ]}
            >
                <InputNumber ref={useRef(inputRef.current!.input)} onPressEnter={save} onBlur={save} />
            </Form.Item>
        ) : (
            <Form.Item
                style={{ margin: 0 }}
                name={dataIndex}
                rules={[
                    {
                        required: true,
                        message: `${title} is required.`,
                    },
                ]}
            >
                <Input ref={inputRef} onPressEnter={save} onBlur={save} />
            </Form.Item>
        ) : (
            <div className="editable-cell-value-wrap" style={{ height: "32px", lineHeight: "32px", verticalAlign: "middle" }} onClick={toggleEdit}>
                {children}
            </div>
        )
    }

    return <td {...rest}>{childNode}</td>
}


const ApexChart: React.FC<ApexOptions> = (options) => {
    return (
        <ReactApexChart series={options.series} options={options}/>
    )
}


const { RangePicker } = DatePicker;


type EditableTableProps = Parameters<typeof Table<IComponentStockAllocDataType>>[0]
type IComponentStockAllocDataType = IComponentStockAlloc & { key: React.Key }
type ColumnTypes = Exclude<EditableTableProps['columns'], undefined>


export const PortfolioManager: React.FC = () => {
    const analyzedProperties = ["income", "return"] as const

    const [dataSource, setDataSource] = useState<IComponentStockAllocDataType[]>([])
    const [count, setCount] = useState(0)
    const [fromDate, setFromDate] = useState("")
    const [toDate, setToDate] = useState("")
    const [chartData, setChartData] = useState({} as ApexOptions)
    const [displayChart, setDisplayChart] = useState(false)
    const [chartDisplayProp, setChartDisplayProp] = useState(analyzedProperties[0] as typeof analyzedProperties[number])

    const handleDelete = (key: React.Key) => {
        const newDataSource = dataSource.filter(item => item.key !== key)
        setDataSource(newDataSource)
    }

    const defaultColumns: (ColumnTypes[number] &
        { editable?: boolean, isNumber?: boolean, dataIndex: string })[] = [
        {
            title: 'symbol',
            dataIndex: 'symbol',
            width: '30%',
            editable: true
        },
        {
            title: 'security',
            dataIndex: 'security'
        },
        {
            title: 'GICSSector',
            dataIndex: 'GICSSector'
        },
        {
            title: 'GICSSubIndustry',
            dataIndex: 'GICSSubIndustry'
        },
        {
            title: 'investment',
            dataIndex: 'investment',
            editable: true,
            isNumber: true
        },
        {
            title: 'operation',
            dataIndex: 'operation',
            render: (_, record: { key: React.Key }) =>
                dataSource.length > 0 ? (
                    <Popconfirm title="Delete?" onConfirm={() => handleDelete(record.key)}>
                        <a>Delete</a>
                    </Popconfirm>
                ) : null
        }
    ]

    const handleAdd = () => {
        const newComponentStockAllocData: IComponentStockAllocDataType = {
            key: count,
            symbol: "",
            security: "",
            GICSSector: "",
            GICSSubIndustry: "",
            investment: 0
        }

        setDataSource([...dataSource, newComponentStockAllocData])
        setCount(count + 1)
    }

    const verifyInvestment = (investment: {[symbol: string]: number | string}) => {
        let sum = 0
        for (const allocation of Object.values(investment)) {
            if (typeof allocation !== "number") {
                sum += parseFloat(allocation)
            } else {
                sum += allocation
            }
        }
        console.log(sum)
        return sum === 1
    }

    const setPortfolioAnalysisChartData = (result: IPortfolioAnalyzeResult) => {
        const options: ApexOptions = {
            chart: {
                height: 350,
                type: 'line',
                dropShadow: {
                    enabled: true,
                    color: '#000',
                    top: 18,
                    left: 7,
                    blur: 10,
                    opacity: 0.2
                },
                toolbar: {
                    show: false
                }
            },
            colors: ['#77B6EA', '#545454'],
            dataLabels: {
                enabled: true,
            },
            grid: {
                borderColor: '#e7e7e7',
                row: {
                    colors: ['#f3f3f3', 'transparent'], // takes an array which will be repeated on columns
                    opacity: 0.5
                }
            },
            markers: {
                size: 1
            },
            xaxis: {
                type: "datetime",
                title: {
                    text: "date"
                }
            },
            yaxis: {
                title: {
                    text: chartDisplayProp
                }
            },
            legend: {
                position: 'top',
                horizontalAlign: 'right',
                floating: true,
                offsetY: -25,
                offsetX: -5
            }
        }

        switch (chartDisplayProp) {
            case "income":
                options.series = [{
                    name: "Portfolio Income",
                    data: result.portfolioIncomes.income.map(timedResult => {
                        return {
                            x: new Date(timedResult.time).getTime(),
                            y: Math.round((timedResult.data + Number.EPSILON) * 100) / 100
                        }
                    })
                }, {
                    name: "SPY Income",
                    data: result.spyIncomes.income.map(timedResult => {
                        return {
                            x: new Date(timedResult.time).getTime(),
                            y: Math.round((timedResult.data + Number.EPSILON) * 100) / 100
                        }
                    })
                }]
                options.title = {
                    text: 'Income of Portfolio with SPY Income as basic',
                    align: 'left'
                }
                break
            case "return":
                options.series = [{
                    name: "Portfolio Return",
                    data: result.portfolioReturns.returns.map(timedResult => {
                        return {
                            x: new Date(timedResult.time).getTime(),
                            y: Math.round((timedResult.data + Number.EPSILON) * 100) / 100
                        }
                    })
                }, {
                    name: "SPY Income",
                    data: result.portfolioReturns.returns.map(timedResult => {
                        return {
                            x: new Date(timedResult.time).getTime(),
                            y: Math.round((timedResult.data + Number.EPSILON) * 100) / 100
                        }
                    })
                }]
                options.title = {
                    text: 'Return of Portfolio with SPY Return as basic',
                    align: 'left'
                }
                break
        }

        setChartData(options)
    }

    const handleAnalyze = async () => {
        const investment: {[symbol: string]: number} = {}

        dataSource.forEach(data => investment[data.symbol] = data.investment)

        if (!fromDate) {
            alert("please verify from date")
            return
        }
        if (!toDate) {
            alert("please verify to date")
            return
        }
        if (!verifyInvestment(investment)) {
            alert("please verify investments, allocation sum not equal to 1")
            return
        }

        const portfolio: IPortfolio = {
            id: null,
            from: fromDate,
            to: toDate,
            investment
        }

        const result = await requestAnalyzedPortfolio(portfolio)

        setPortfolioAnalysisChartData(result)
        setDisplayChart(true)
    }

    const handleSave = async (row: IComponentStockAllocDataType) => {
        const newData = [...dataSource]
        const index = newData.findIndex(item => row.key === item.key)
        const item = newData[index]

        const data = await requestComponentStock(row.symbol)
        row = {
            ...row,
            security: data.security,
            GICSSector: data.GICSSector,
            GICSSubIndustry: data.GICSSubIndustry
        }

        newData.splice(index, 1, {
            ...item,
            ...row
        })
        setDataSource(newData)
    }

    const components = {
        body: {
            row: EditableRow,
            cell: EditableCell,
        }
    }

    const columns = defaultColumns.map(col => {
        if (!col.editable) return col
        return {
            ...col,
            onCell: (record: IComponentStockAllocDataType) => {
                return {
                    record,
                    editable: col.editable,
                    dataIndex: col.dataIndex,
                    title: col.title,
                    handleSave
                }
            }
        }
    })

    const stringToDate = (dateString: string) => {
        const from = dateString.split('-')
        return new Date(parseInt(from[0]), parseInt(from[1]) - 1, parseInt(from[2]))
    }

    const onDateChange = (
        value: DatePickerProps['value'] | RangePickerProps['value'],
        dateString: [string, string] | string,
    ) => {
        setFromDate(stringToDate(dateString[0]).toISOString())
        setToDate(stringToDate(dateString[1]).toISOString())
    };

    const onPropChange = ({ target: { value } }: RadioChangeEvent) => {
        setChartDisplayProp(value);
    }

    return (
        <div>
            <Button onClick={handleAdd} type="primary" style={{ marginBottom: 16 }}>
                Add a row
            </Button>
            <RangePicker onChange={onDateChange}/>
            <Button onClick={handleAnalyze} type="primary" style={{ marginBottom: 16 }}>
                Analyze
            </Button>
            <Radio.Group buttonStyle={"solid"} defaultValue={analyzedProperties[0]} onChange={onPropChange}>
                <Radio.Button value={analyzedProperties[0]}>analyzedProperties[0]</Radio.Button>
                <Radio.Button value={analyzedProperties[1]}>analyzedProperties[1]</Radio.Button>
            </Radio.Group>
            <Table
                components={components}
                rowClassName={() => 'editable-row'}
                bordered
                dataSource={dataSource}
                columns={columns as ColumnTypes}
            />
            { displayChart && ApexChart(chartData) }
        </div>
    );
}


