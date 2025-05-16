import { useState, useEffect } from "react";
import { IoWalletOutline } from "react-icons/io5";
import { FaRegMoneyBillAlt } from "react-icons/fa";
import { HiArrowTrendingUp, HiArrowTrendingDown  } from "react-icons/hi2";
import {
    Chart as ChartJS,
    CategoryScale,
    LinearScale,
    BarElement,
    LineElement,
    PointElement,
    Title,
    Tooltip,
    Legend,
} from 'chart.js';
import { Chart } from 'react-chartjs-2';
import TransactionModal from "./TransactionModal";
import axios from "axios";

import "../../styles/dashboard.css";

ChartJS.register(
    CategoryScale,
    LinearScale,
    BarElement,
    LineElement,
    PointElement,
    Title,
    Tooltip,
    Legend
);

interface Transaction {
    name: string;
    amount: number;
    transactionType: string;
    transactionCategory: string;
    date: string;
}

export default function Dashboard() {
    const [data, setData] = useState<Transaction[]>([]);
    const [selectedMonth, setSelectedMonth] = useState<number>(new Date().getMonth());
    const [selectedYear, setSelectedYear] = useState<number>(new Date().getFullYear());
    const [modalOpen, setModalOpen] = useState<boolean>(false);
    const [filteredTransactions, setFilteredTransactions] = useState<Transaction[]>([]);

    const months = [
        "Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho",
        "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"
    ];

    const handleChangeMonth = (event: React.ChangeEvent<HTMLSelectElement>) => {
        setSelectedMonth(Number(event.target.value));
    };

    const filterTransactionsByMonth = (transactions: Transaction[]) => {
        return transactions.filter(transaction => {
            const transactionDate = new Date(transaction.date);
            return (
                transactionDate.getMonth() === selectedMonth &&
                transactionDate.getFullYear() === selectedYear
            );
        });
    };

    useEffect(() => {
        if (data.length > 0) {
            const filtered = filterTransactionsByMonth(data);
            setFilteredTransactions(filtered);
        }
    }, [data, selectedMonth, selectedYear]);

    const getTransactions = async () => {
        try {
            const token = localStorage.getItem('jwtToken');

            const response = await axios.get(`http://localhost:8080/api/transactions`, {
                headers: {
                    'Authorization': token
                }
            });

            setData(response.data);
        } catch (error) {
            console.log(error);
        }
    };

    useEffect(() => {
        getTransactions();
    }, []);

    const addNewTransaction = () => {
        setModalOpen(true);
    };

    const closeModal = () => {
        setModalOpen(false);
        getTransactions();
    };

    const totalDespesas = data
        .filter(transaction => transaction.transactionType === 'DESPESA')
        .reduce((total, transaction) => total + transaction.amount, 0);

    const totalReceitas = data
        .filter(transaction => transaction.transactionType === 'DEPOSITO')
        .reduce((total, transaction) => total + transaction.amount, 0);

    const totalAmount = totalReceitas + (-totalDespesas);

    // Agrupar transações por mês
    const monthlyData = data.reduce((acc, transaction) => {
        const date = new Date(transaction.date);
        const monthKey = months[date.getMonth()];
        
        if (!acc[monthKey]) {
            acc[monthKey] = {
                despesas: 0,
                depositos: 0,
                saldo: 0
            };
        }

        if (transaction.transactionType === 'DESPESA') {
            acc[monthKey].despesas += transaction.amount;
            acc[monthKey].saldo -= transaction.amount;
        } else {
            acc[monthKey].depositos += transaction.amount;
            acc[monthKey].saldo += transaction.amount;
        }

        return acc;
    }, {} as Record<string, { despesas: number; depositos: number; saldo: number }>);

    const chartMonths = Object.keys(monthlyData);

    const chartData = {
        labels: chartMonths,
        datasets: [
            {
                type: 'bar' as const,
                label: 'Depósitos',
                data: chartMonths.map(month => monthlyData[month].depositos),
                backgroundColor: 'rgba(34, 197, 94, 0.75)',
                borderColor: 'rgba(34, 197, 94, 0.9)',
                borderWidth: 2,
                borderRadius: {
                    topLeft: 6,
                    topRight: 6,
                    bottomLeft: 0,
                    bottomRight: 0
                },
                order: 2
            },
            {
                type: 'bar' as const,
                label: 'Despesas',
                data: chartMonths.map(month => -monthlyData[month].despesas),
                backgroundColor: 'rgba(239, 68, 68, 0.75)',
                borderColor: 'rgba(239, 68, 68, 0.9)',
                borderWidth: 2,
                borderRadius: {
                    topLeft: 6,
                    topRight: 6,
                    bottomLeft: 0,
                    bottomRight: 0
                },
                order: 1
            },
            {
                type: 'line' as const,
                label: 'Saldo Mensal',
                data: chartMonths.map(month => monthlyData[month].saldo),
                borderColor: 'rgba(59, 130, 246, 0.9)',
                backgroundColor: 'rgba(59, 130, 246, 0.9)',
                borderWidth: 3,
                pointRadius: 5,
                pointHoverRadius: 8,
                pointBackgroundColor: 'white',
                pointBorderWidth: 2,
                pointStyle: 'circle',
                tension: 0.3,
                order: 0,
                yAxisID: 'y1'
            }
        ]
    };

    const chartOptions = {
        responsive: true,
        maintainAspectRatio: false,
        interaction: {
            intersect: false,
            mode: 'index' as const
        },
        plugins: {
            legend: {
                display: true,
                position: 'top' as const,
                labels: {
                    padding: 20,
                    usePointStyle: true,
                    font: {
                        size: 13
                    }
                }
            },
            title: {
                display: true,
                text: 'Análise Financeira Mensal',
                padding: {
                    top: 10,
                    bottom: 30
                },
                color: '#2d3748',
                font: {
                    size: 18,
                    weight: 'bold' as const
                }
            },
            tooltip: {
                backgroundColor: 'rgba(255, 255, 255, 0.95)',
                titleColor: '#1a202c',
                bodyColor: '#4a5568',
                borderColor: 'rgba(0, 0, 0, 0.1)',
                borderWidth: 1,
                padding: 12,
                boxPadding: 6,
                usePointStyle: true,
                callbacks: {
                    label: function(context: any) {
                        let value = context.raw;
                        if (context.dataset.label === 'Despesas') {
                            value = Math.abs(value);
                        }
                        return `${context.dataset.label}: R$ ${value.toFixed(2)}`;
                    }
                }
            }
        },
        scales: {
            y: {
                type: 'linear' as const,
                display: true,
                position: 'left' as const,
                grid: {
                    color: 'rgba(0, 0, 0, 0.05)',
                    drawOnChartArea: true,
                    zeroLineColor: 'rgba(0, 0, 0, 0.2)',
                    zeroLineWidth: 2
                },
                border: {
                    dash: [4, 4]
                },
                ticks: {
                    callback: function(value: number | string) {
                        if (typeof value === 'number') {
                            return `R$ ${Math.abs(value).toFixed(2)}`;
                        }
                        return value;
                    },
                    font: {
                        size: 12
                    },
                    padding: 8
                }
            },
            y1: {
                type: 'linear' as const,
                display: true,
                position: 'right' as const,
                grid: {
                    drawOnChartArea: false
                },
                border: {
                    dash: [4, 4]
                },
                beginAtZero: true,
                ticks: {
                    callback: function(value: number | string) {
                        if (typeof value === 'number') {
                            return `R$ ${value.toFixed(2)}`;
                        }
                        return value;
                    },
                    font: {
                        size: 12
                    },
                    padding: 8
                }
            },
            x: {
                grid: {
                    display: false
                },
                border: {
                    dash: [4, 4]
                },
                ticks: {
                    font: {
                        size: 12
                    },
                    padding: 8
                }
            }
        }
    };

    const formatDate = (dateString: string) => {
        const date = new Date(dateString);
        return date.toLocaleDateString('pt-BR', {
            day: '2-digit',
            month: 'short',
            year: 'numeric'
        });
    };

    return (
        <>
            <div className="dashboard-header-container">
                <div className="dashboard-title">
                    Dashboard
                </div>
            </div>

            <div className="dashboard-main-container">
                <div className="dashboard-top-container">
                    <div className="dashboard-total-amount-container">
                        <div className="dashboard-total-amount-content">
                            <h3><IoWalletOutline /> Saldo</h3>
                            <div className="dashboard-total-number">
                                <p>R$ {totalAmount.toFixed(2)}</p>
                                <button className="add-transaction-button" onClick={addNewTransaction}>
                                    Adicionar transação
                                </button>
                            </div>
                        </div>
                        <div className="dashboard-amount-content">
                            <div className="amount-section">
                                <h3><HiArrowTrendingUp color="green"/> Receitas</h3>
                                <p>R$ {totalReceitas.toFixed(2)}</p>
                            </div>
                            <div className="amount-section">
                                <h3><HiArrowTrendingDown color="red"/> Despesas</h3>
                                <p>R$ {totalDespesas.toFixed(2)}</p>
                            </div>
                        </div>
                        <div className="dashboard-chart-container">
                            {data.length > 0 ? (
                                <div className="dashboard-chart">
                                    <Chart
                                        type="bar"
                                        data={chartData}
                                        options={chartOptions}
                                        height={400}
                                    />
                                </div>
                            ) : (
                                <div className="dashboard-graphic-container no-data">
                                    <div className="dashboard-graphic-content">
                                        <div className="no-data-message">
                                            <p>Adicione transações para visualizar o gráfico</p>
                                        </div>
                                    </div>
                                </div>
                            )}
                        </div>
                    </div>

                    {/* Seção de Últimas Transações */}
                    <div className="dashboard-last-transactions-container">
                        <div className="dashboard-last-transactions-content">
                            <div className="transactions-header">
                                <h3>Últimas Transações</h3>
                                <div className="select-container">
                                    <select
                                        value={selectedMonth}
                                        onChange={handleChangeMonth}
                                        className="custom-select"
                                    >
                                        {months.map((month, index) => (
                                            <option key={month} value={index}>
                                                {month}
                                            </option>
                                        ))}
                                    </select>
                                </div>
                            </div>
                            <div className="last-transactions">
                                {filteredTransactions.length > 0 ? (
                                    filteredTransactions.map((transaction, index) => (
                                        <div key={index}>
                                            <div style={{gap: '1rem', alignItems: 'center'}}>
                                                <FaRegMoneyBillAlt />
                                                {transaction.name}
                                                <div style={{marginLeft: '1rem'}}>
                                                    {formatDate(transaction.date)}
                                                </div>
                                            </div>
                                            <span style={{
                                                color: transaction.transactionType === 'DESPESA' ? '#ef4444' : '#22c55e'
                                            }}>
                                                {transaction.transactionType === "DESPESA" ? "-" : "+"}
                                                R$ {transaction.amount.toFixed(2)}
                                            </span>
                                        </div>
                                    ))
                                ) : (
                                    <div style={{ padding: "20px", color: "#666", textAlign: "center" }}>
                                        Nenhuma transação encontrada para este mês.
                                    </div>
                                )}
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            {modalOpen && <TransactionModal onClose={closeModal} />}
        </>
    );
}