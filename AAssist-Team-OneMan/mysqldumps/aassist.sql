-- phpMyAdmin SQL Dump
-- version 4.6.4
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Aug 01, 2018 at 03:16 PM
-- Server version: 5.7.14
-- PHP Version: 5.6.25

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `aassist`
--

-- --------------------------------------------------------

--
-- Table structure for table `accounts`
--

CREATE TABLE `accounts` (
  `userid` int(100) NOT NULL,
  `bankid` int(100) NOT NULL,
  `username` varchar(255) DEFAULT NULL,
  `passwd` varchar(100) NOT NULL,
  `token` varchar(256) NOT NULL,
  `entryno` int(11) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Dumping data for table `accounts`
--

INSERT INTO `accounts` (`userid`, `bankid`, `username`, `passwd`, `token`, `entryno`) VALUES
(1, 2018001, 'intel', 'password', '6c3dbd545aa4fa7e5a2c73c36c037aded20ba3ed6d68c4275133a1c1cbc52337', 1),
(1, 2018002, 'intel', 'password', 'c02118610b48289444e459ed331f27ac8709ef49390de166fe86a61be86db99c', 2),
(2, 2018001, 'parth', 'password', '848f1c1401bbbf860e8e9da7756d3f38d19a8f8c34fe5222dba3fc40d529a6f4', 3);

-- --------------------------------------------------------

--
-- Table structure for table `banks`
--

CREATE TABLE `banks` (
  `bankid` int(11) NOT NULL,
  `name` varchar(20) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `defaultaccs`
--

CREATE TABLE `defaultaccs` (
  `userid` int(11) NOT NULL,
  `accno` bigint(20) NOT NULL,
  `ifsc` varchar(11) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Dumping data for table `defaultaccs`
--

INSERT INTO `defaultaccs` (`userid`, `accno`, `ifsc`) VALUES
(1, 18923467823, 'SBIN0009043'),
(2, 17933577822, 'SBIN0016682');

-- --------------------------------------------------------

--
-- Table structure for table `pull_transfer`
--

CREATE TABLE `pull_transfer` (
  `fromemail` varchar(100) NOT NULL,
  `toemail` varchar(100) NOT NULL,
  `amount` int(11) NOT NULL,
  `pt_id` int(11) NOT NULL,
  `fetched` int(11) NOT NULL DEFAULT '0'
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Dumping data for table `pull_transfer`
--

INSERT INTO `pull_transfer` (`fromemail`, `toemail`, `amount`, `pt_id`, `fetched`) VALUES
('friends@intel.com', 'www.ppc007logs@gmail.com', 20, 1, 1),
('www.ppc007logs@gmail.com', 'friends@intel.com', 20, 3, 1),
('friends@intel.com', 'www.ppc007logs@gmail.com', 40, 5, 1),
('friends@intel.com', 'www.ppc007logs@gmail.com', 40, 6, 1),
('www.ppc007logs@gmail.com', 'friends@intel.com', 40, 7, 1),
('www.ppc007logs@gmail.com', 'friends@intel.com', 40, 8, 0),
('friends@intel.com', 'www.ppc007logs@gmail.com', 20, 9, 0),
('friends@intel.com', 'www.ppc007logs@gmail.com', 20, 10, 0),
('friends@intel.com', 'www.ppc007logs@gmail.com', 20, 11, 0),
('friends@intel.com', 'www.ppc007logs@gmail.com', 20, 12, 0),
('friends@intel.com', 'www.ppc007logs@gmail.com', 20, 13, 0),
('friends@intel.com', 'www.ppc007logs@gmail.com', 20, 14, 0),
('friends@intel.com', 'www.ppc007logs@gmail.com', 20, 15, 0),
('friends@intel.com', 'www.ppc007logs@gmail.com', 20, 16, 0),
('friends@intel.com', 'www.ppc007logs@gmail.com', 20, 17, 0),
('friends@intel.com', 'www.ppc007logs@gmail.com', 20, 18, 0),
('friends@intel.com', 'www.ppc007logs@gmail.com', 20, 19, 0),
('friends@intel.com', 'www.ppc007logs@gmail.com', 20, 20, 0),
('friends@intel.com', 'www.ppc007logs@gmail.com', 20, 21, 0),
('friends@intel.com', 'www.ppc007logs@gmail.com', 20, 22, 0),
('friends@intel.com', 'www.ppc007logs@gmail.com', 20, 23, 0),
('friends@intel.com', 'www.ppc007logs@gmail.com', 20, 24, 0),
('friends@intel.com', 'www.ppc007logs@gmail.com', 20, 25, 0),
('friends@intel.com', 'www.ppc007logs@gmail.com', 20, 26, 0),
('friends@intel.com', 'www.ppc007logs@gmail.com', 20, 27, 0),
('friends@intel.com', 'www.ppc007logs@gmail.com', 20, 28, 0),
('friends@intel.com', 'www.ppc007logs@gmail.com', 20, 29, 0),
('friends@intel.com', 'www.ppc007logs@gmail.com', 20, 30, 0),
('friends@intel.com', 'www.ppc007logs@gmail.com', 20, 31, 0),
('friends@intel.com', 'www.ppc007logs@gmail.com', 20, 32, 0),
('friends@intel.com', 'www.ppc007logs@gmail.com', 20, 33, 0),
('friends@intel.com', 'www.ppc007logs@gmail.com', 20, 34, 0),
('friends@intel.com', 'www.ppc007logs@gmail.com', 20, 35, 0),
('friends@intel.com', 'www.ppc007logs@gmail.com', 20, 36, 0),
('friends@intel.com', 'www.ppc007logs@gmail.com', 20, 37, 0),
('friends@intel.com', 'www.ppc007logs@gmail.com', 20, 38, 0),
('friends@intel.com', 'www.ppc007logs@gmail.com', 20, 39, 0),
('friends@intel.com', 'www.ppc007logs@gmail.com', 20, 40, 0),
('friends@intel.com', 'www.ppc007logs@gmail.com', 20, 41, 0),
('friends@intel.com', 'www.ppc007logs@gmail.com', 20, 42, 0),
('friends@intel.com', 'www.ppc007logs@gmail.com', 20, 43, 0),
('friends@intel.com', 'www.ppc007logs@gmail.com', 20, 44, 0),
('friends@intel.com', 'www.ppc007logs@gmail.com', 20, 45, 0),
('friends@intel.com', 'www.ppc007logs@gmail.com', 20, 46, 0),
('friends@intel.com', 'www.ppc007logs@gmail.com', 20, 47, 0),
('friends@intel.com', 'www.ppc007logs@gmail.com', 20, 48, 0),
('friends@intel.com', 'www.ppc007logs@gmail.com', 20, 49, 0),
('friends@intel.com', 'www.ppc007logs@gmail.com', 20, 50, 0),
('friends@intel.com', 'www.ppc007logs@gmail.com', 20, 51, 0),
('friends@intel.com', 'www.ppc007logs@gmail.com', 20, 52, 0),
('friends@intel.com', 'www.ppc007logs@gmail.com', 20, 53, 0),
('friends@intel.com', 'www.ppc007logs@gmail.com', 20, 54, 0),
('friends@intel.com', 'www.ppc007logs@gmail.com', 20, 55, 0),
('friends@intel.com', 'www.ppc007logs@gmail.com', 20, 56, 0),
('friends@intel.com', 'www.ppc007logs@gmail.com', 20, 57, 0),
('friends@intel.com', 'www.ppc007logs@gmail.com', 20, 58, 0),
('friends@intel.com', 'www.ppc007logs@gmail.com', 20, 59, 0),
('friends@intel.com', 'www.ppc007logs@gmail.com', 20, 60, 0),
('friends@intel.com', 'www.ppc007logs@gmail.com', 20, 61, 0),
('friends@intel.com', 'www.ppc007logs@gmail.com', 20, 62, 0),
('www.ppc007logs@gmail.com', 'friends@intel.com', 20, 63, 0),
('friends@intel.com', 'www.ppc007logs@gmail.com', 200, 64, 0),
('friends@intel.com', 'www.ppc007logs@gmail.com', 20, 65, 0),
('friends@intel.com', 'www.ppc007logs@gmail.com', 20, 66, 0),
('friends@intel.com', 'www.ppc007logs@gmail.com', 20, 67, 0),
('www.ppc007logs@gmail.com', 'friends@intel.com', 20, 68, 0),
('friends@intel.com', 'www.ppc007logs@gmail.com', 20, 69, 0),
('friends@intel.com', 'www.ppc007logs@gmail.com', 20, 70, 0),
('friends@intel.com', 'www.ppc007logs@gmail.com', 20, 71, 0),
('friends@intel.com', 'www.ppc007logs@gmail.com', 20, 72, 0),
('friends@intel.com', 'www.ppc007logs@gmail.com', 200, 73, 0);

-- --------------------------------------------------------

--
-- Table structure for table `quicksend`
--

CREATE TABLE `quicksend` (
  `name` varchar(100) NOT NULL,
  `acc` bigint(11) NOT NULL,
  `ifsc` varchar(11) NOT NULL,
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `createdby` int(11) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Dumping data for table `quicksend`
--

INSERT INTO `quicksend` (`name`, `acc`, `ifsc`, `created`, `createdby`) VALUES
('parth pratim', 17933577822, 'SBIN0016682', '2018-03-30 06:55:16', 1),
('supratim', 159214268236218, 'UTIB0003095', '2018-07-22 11:17:50', 1),
('supratim', 17933577822, 'SBIN0016682', '2018-07-26 07:49:56', 1),
('Naveen', 26825467832, 'SBIN0007043', '2018-07-28 15:04:28', 1);

-- --------------------------------------------------------

--
-- Table structure for table `transactions`
--

CREATE TABLE `transactions` (
  `transid` int(100) NOT NULL,
  `userid` int(100) NOT NULL,
  `remitbank` int(100) NOT NULL,
  `transtype` varchar(11) NOT NULL,
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `remitacc` bigint(11) NOT NULL,
  `benefifsc` varchar(11) NOT NULL,
  `benefacc` bigint(11) NOT NULL,
  `transamt` int(11) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Dumping data for table `transactions`
--

INSERT INTO `transactions` (`transid`, `userid`, `remitbank`, `transtype`, `timestamp`, `remitacc`, `benefifsc`, `benefacc`, `transamt`) VALUES
(28, 1, 2018001, 'INTER_BANK', '2018-07-28 15:01:13', 17823467823, 'UTIB0003095', 159214268236218, 20),
(27, 1, 2018001, 'INTRA_BANK', '2018-07-28 14:39:55', 18923467823, 'UTIB0003095', 159214268236218, 20),
(26, 2, 2018001, 'INTRA_BANK', '2018-07-26 17:22:15', 17933577822, 'SBIN0009043', 18923467823, 20),
(25, 1, 2018001, 'INTRA_BANK', '2018-07-25 16:07:15', 18923467823, 'SBIN0016682', 17933577822, 20),
(24, 1, 2018001, 'INTRA_BANK', '2018-07-25 16:05:21', 18923467823, 'SBIN0016682', 17933577822, 20),
(23, 1, 2018001, 'INTRA_BANK', '2018-07-24 13:19:30', 18923467823, 'SBIN0016682', 17933577822, 20),
(22, 1, 2018001, 'INTRA_BANK', '2018-07-24 13:15:42', 18923467823, 'SBIN0016682', 17933577822, 20),
(21, 1, 2018001, 'INTRA_BANK', '2018-07-24 13:08:42', 18923467823, 'SBIN0016682', 17933577822, 1000),
(20, 1, 2018001, 'INTRA_BANK', '2018-07-24 13:07:08', 18923467823, 'SBIN0016682', 17933577822, 1000),
(19, 1, 2018001, 'INTRA_BANK', '2018-07-24 13:04:23', 18923467823, 'SBIN0016682', 17933577822, 20),
(18, 1, 2018001, 'INTRA_BANK', '2018-07-24 13:03:32', 18923467823, 'SBIN0016682', 17933577822, 20),
(17, 1, 2018001, 'INTRA_BANK', '2018-07-24 13:02:04', 18923467823, 'SBIN0016682', 17933577822, 587),
(16, 1, 2018001, 'INTRA_BANK', '2018-07-22 15:04:06', 18923467823, 'SBIN0016682', 17933577822, 20),
(15, 1, 2018001, 'INTRA_BANK', '2018-07-22 14:58:56', 18923467823, 'SBIN0016682', 17933577822, 20),
(29, 1, 2018001, 'INTRA_BANK', '2018-07-28 20:08:02', 18923467823, 'SBIN0007043', 26825467832, 40);

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `id` int(100) NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `passwd` varchar(100) DEFAULT NULL,
  `token` varchar(256) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`id`, `name`, `email`, `passwd`, `token`) VALUES
(1, 'Intel ', 'friends@intel.com', 'password', 'fa08f366b068e7e679df10492e1337598a6f27d19aa6258f3c8a48cc8bbb1b04'),
(2, 'Parth Pratim Chatterjee', 'www.ppc007logs@gmail.com', 'password', '94df97959544eb298a740b495bba949759f6aade088bfa72dd14693bab4e1688');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `accounts`
--
ALTER TABLE `accounts`
  ADD PRIMARY KEY (`entryno`);

--
-- Indexes for table `banks`
--
ALTER TABLE `banks`
  ADD PRIMARY KEY (`bankid`);

--
-- Indexes for table `defaultaccs`
--
ALTER TABLE `defaultaccs`
  ADD PRIMARY KEY (`userid`);

--
-- Indexes for table `pull_transfer`
--
ALTER TABLE `pull_transfer`
  ADD PRIMARY KEY (`pt_id`);

--
-- Indexes for table `quicksend`
--
ALTER TABLE `quicksend`
  ADD PRIMARY KEY (`created`);

--
-- Indexes for table `transactions`
--
ALTER TABLE `transactions`
  ADD PRIMARY KEY (`transid`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `email` (`email`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `accounts`
--
ALTER TABLE `accounts`
  MODIFY `entryno` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;
--
-- AUTO_INCREMENT for table `pull_transfer`
--
ALTER TABLE `pull_transfer`
  MODIFY `pt_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=74;
--
-- AUTO_INCREMENT for table `transactions`
--
ALTER TABLE `transactions`
  MODIFY `transid` int(100) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=30;
--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `id` int(100) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
