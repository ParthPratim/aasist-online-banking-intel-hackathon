-- phpMyAdmin SQL Dump
-- version 4.6.4
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Aug 01, 2018 at 03:33 PM
-- Server version: 5.7.14
-- PHP Version: 5.6.25

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `axis`
--

-- --------------------------------------------------------

--
-- Table structure for table `accounts`
--

CREATE TABLE `accounts` (
  `userid` int(11) NOT NULL,
  `user` varchar(11) NOT NULL,
  `accno` bigint(20) NOT NULL,
  `cifno` bigint(20) NOT NULL,
  `balance` int(11) NOT NULL,
  `acctype` tinyint(11) NOT NULL,
  `ifsc` varchar(11) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Dumping data for table `accounts`
--

INSERT INTO `accounts` (`userid`, `user`, `accno`, `cifno`, `balance`, `acctype`, `ifsc`) VALUES
(4, 'kruti', 178122386236211, 2255, 140, 1, 'UTIB0003075'),
(2, 'parth', 159214268236218, 1543, 2068, 1, 'UTIB0003095');

-- --------------------------------------------------------

--
-- Table structure for table `linked`
--

CREATE TABLE `linked` (
  `userid` int(11) NOT NULL,
  `name` varchar(100) NOT NULL,
  `accno` bigint(20) NOT NULL,
  `ifsc` varchar(11) NOT NULL,
  `createdat` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `id` int(11) NOT NULL,
  `name` varchar(30) NOT NULL,
  `username` varchar(30) NOT NULL,
  `passwd` varchar(30) NOT NULL,
  `token` varchar(100) NOT NULL,
  `cifno` bigint(20) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`id`, `name`, `username`, `passwd`, `token`, `cifno`) VALUES
(1, 'Intel ', 'intel', 'password', 'c02118610b48289444e459ed331f27ac8709ef49390de166fe86a61be86db99c', 1723),
(2, 'Parth Pratim Chatterjee', 'parth', 'password', 'j02118610bf8289444g459ed331f27jac8709ef49390de166fe86a61be86db99c', 1543),
(3, 'Naveen Bansal', 'naveen', 'password', 'k03118510cf82j9444g4596d331f27jac8709ef49390de166fe86a61be86db99h', 1255),
(4, 'Kruti', 'kruti', 'password', 'd03228510gf81j9555g4596d331f27jdc8709ef49390de166fe86a61be86db99i', 2255);

--
-- Indexes for dumped tables
--

--
-- Indexes for table `accounts`
--
ALTER TABLE `accounts`
  ADD PRIMARY KEY (`accno`);

--
-- Indexes for table `linked`
--
ALTER TABLE `linked`
  ADD PRIMARY KEY (`createdat`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
