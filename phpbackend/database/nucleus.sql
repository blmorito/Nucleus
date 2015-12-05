-- phpMyAdmin SQL Dump
-- version 3.5.2.2
-- http://www.phpmyadmin.net
--
-- Host: 127.0.0.1
-- Generation Time: Dec 05, 2015 at 04:35 PM
-- Server version: 5.5.27
-- PHP Version: 5.4.7

SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Database: `nucleus`
--

-- --------------------------------------------------------

--
-- Table structure for table `activity`
--

CREATE TABLE IF NOT EXISTS `activity` (
  `activity_id` int(11) NOT NULL AUTO_INCREMENT,
  `project_id` int(11) NOT NULL,
  `from_id` int(11) NOT NULL,
  `from_type` varchar(100) COLLATE utf8_unicode_ci NOT NULL,
  `message` varchar(200) COLLATE utf8_unicode_ci NOT NULL,
  `date` datetime NOT NULL,
  PRIMARY KEY (`activity_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `assignment`
--

CREATE TABLE IF NOT EXISTS `assignment` (
  `assign_id` int(11) NOT NULL AUTO_INCREMENT,
  `task_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  PRIMARY KEY (`assign_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `avatars`
--

CREATE TABLE IF NOT EXISTS `avatars` (
  `avatar_id` int(11) NOT NULL AUTO_INCREMENT,
  `avatar_img` mediumblob NOT NULL,
  PRIMARY KEY (`avatar_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `comment`
--

CREATE TABLE IF NOT EXISTS `comment` (
  `comment_id` int(11) NOT NULL AUTO_INCREMENT,
  `comment_type` varchar(50) COLLATE utf8_unicode_ci NOT NULL,
  `comment_type_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `content` varchar(500) COLLATE utf8_unicode_ci NOT NULL,
  `date_posted` datetime NOT NULL,
  PRIMARY KEY (`comment_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `discussion`
--

CREATE TABLE IF NOT EXISTS `discussion` (
  `discussion_id` int(11) NOT NULL AUTO_INCREMENT,
  `project_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `d_subject` varchar(200) COLLATE utf8_unicode_ci NOT NULL DEFAULT '(no subject)',
  `d_body` varchar(2500) COLLATE utf8_unicode_ci NOT NULL,
  `date_posted` datetime NOT NULL,
  PRIMARY KEY (`discussion_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `file`
--

CREATE TABLE IF NOT EXISTS `file` (
  `file_id` int(11) NOT NULL AUTO_INCREMENT,
  `project_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `file_content` mediumblob NOT NULL,
  `file_name` varchar(100) COLLATE utf8_unicode_ci NOT NULL,
  `file_size` varchar(100) COLLATE utf8_unicode_ci NOT NULL,
  `file_type` varchar(100) COLLATE utf8_unicode_ci NOT NULL,
  `file_desc` varchar(300) COLLATE utf8_unicode_ci NOT NULL,
  `mime_type` varchar(100) COLLATE utf8_unicode_ci NOT NULL,
  `date_uploaded` datetime NOT NULL,
  PRIMARY KEY (`file_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `forgot_password`
--

CREATE TABLE IF NOT EXISTS `forgot_password` (
  `email` varchar(200) COLLATE utf8_unicode_ci NOT NULL,
  `token` varchar(200) COLLATE utf8_unicode_ci NOT NULL,
  `token_expiry` datetime NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `goal`
--

CREATE TABLE IF NOT EXISTS `goal` (
  `goal_id` int(11) NOT NULL AUTO_INCREMENT,
  `goal_name` varchar(250) COLLATE utf8_unicode_ci NOT NULL,
  `goal_desc` varchar(200) COLLATE utf8_unicode_ci NOT NULL,
  `project_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `date_created` datetime NOT NULL,
  `date_started` date NOT NULL,
  `date_finished` date NOT NULL,
  `status` varchar(20) COLLATE utf8_unicode_ci NOT NULL,
  PRIMARY KEY (`goal_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `notification`
--

CREATE TABLE IF NOT EXISTS `notification` (
  `notification_id` int(11) NOT NULL AUTO_INCREMENT,
  `to_id` int(11) NOT NULL,
  `from_id` int(11) NOT NULL,
  `message` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `date_made` datetime NOT NULL,
  `status` varchar(200) COLLATE utf8_unicode_ci NOT NULL DEFAULT 'Unread',
  PRIMARY KEY (`notification_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `notifications`
--

CREATE TABLE IF NOT EXISTS `notifications` (
  `notification_id` int(11) NOT NULL AUTO_INCREMENT,
  `project_id` int(11) NOT NULL,
  `from_id` int(11) NOT NULL,
  `to_id` int(11) NOT NULL,
  `type` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `type_id` int(11) NOT NULL,
  `content` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `date` datetime NOT NULL,
  `status` varchar(255) COLLATE utf8_unicode_ci NOT NULL DEFAULT 'Unread',
  PRIMARY KEY (`notification_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `projects`
--

CREATE TABLE IF NOT EXISTS `projects` (
  `project_id` int(11) NOT NULL AUTO_INCREMENT,
  `project_name` varchar(200) COLLATE utf8_unicode_ci NOT NULL,
  `project_desc` varchar(250) COLLATE utf8_unicode_ci NOT NULL,
  `project_created` datetime NOT NULL,
  `workspace_id` int(11) NOT NULL,
  `project_deadline` date NOT NULL,
  `project_status` varchar(250) COLLATE utf8_unicode_ci NOT NULL DEFAULT 'In progress',
  `date_finished` date NOT NULL,
  PRIMARY KEY (`project_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `projects_users`
--

CREATE TABLE IF NOT EXISTS `projects_users` (
  `project_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `date_joined` datetime NOT NULL,
  `user_level_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `task`
--

CREATE TABLE IF NOT EXISTS `task` (
  `task_id` int(11) NOT NULL AUTO_INCREMENT,
  `goal_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `task_name` varchar(200) COLLATE utf8_unicode_ci NOT NULL,
  `date_created` datetime NOT NULL,
  `date_started` date NOT NULL,
  `date_finished` date NOT NULL,
  `date_due` date NOT NULL,
  `task_status` varchar(200) COLLATE utf8_unicode_ci NOT NULL DEFAULT 'Open',
  `completed_by` int(11) NOT NULL,
  `started_by` int(11) DEFAULT NULL,
  PRIMARY KEY (`task_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `tbl_users`
--

CREATE TABLE IF NOT EXISTS `tbl_users` (
  `user_id` int(11) NOT NULL AUTO_INCREMENT,
  `email` varchar(200) CHARACTER SET latin1 NOT NULL,
  `password` varchar(200) CHARACTER SET latin1 NOT NULL,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `tbl_usersinfo`
--

CREATE TABLE IF NOT EXISTS `tbl_usersinfo` (
  `user_id` int(20) NOT NULL,
  `full_name` varchar(200) COLLATE utf8_unicode_ci NOT NULL,
  `comp_org` varchar(200) COLLATE utf8_unicode_ci NOT NULL,
  `date_created` date NOT NULL,
  `recent_ws` int(11) NOT NULL,
  `avatar_id` int(11) NOT NULL,
  UNIQUE KEY `user_id` (`user_id`),
  KEY `user_id_2` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `user_levels`
--

CREATE TABLE IF NOT EXISTS `user_levels` (
  `user_level_id` int(11) NOT NULL AUTO_INCREMENT,
  `user_level` varchar(200) COLLATE utf8_unicode_ci NOT NULL,
  PRIMARY KEY (`user_level_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci AUTO_INCREMENT=4 ;

--
-- Dumping data for table `user_levels`
--

INSERT INTO `user_levels` (`user_level_id`, `user_level`) VALUES
(1, 'Creator'),
(2, 'Administrator'),
(3, 'Member');

-- --------------------------------------------------------

--
-- Table structure for table `workspaces`
--

CREATE TABLE IF NOT EXISTS `workspaces` (
  `workspace_id` int(11) NOT NULL AUTO_INCREMENT,
  `workspace_name` varchar(200) COLLATE utf8_unicode_ci NOT NULL,
  `workspace_desc` varchar(200) COLLATE utf8_unicode_ci NOT NULL,
  `ws_date_created` datetime NOT NULL,
  PRIMARY KEY (`workspace_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci AUTO_INCREMENT=90 ;

--
-- Dumping data for table `workspaces`
--

INSERT INTO `workspaces` (`workspace_id`, `workspace_name`, `workspace_desc`, `ws_date_created`) VALUES
(17, 'Fuzzyyyyy', '...hhhh', '2015-08-19 00:00:00'),
(18, 'Sniper''s WS', '', '2015-08-21 00:00:00'),
(19, 'Fuzzywubz', '...', '2015-08-24 00:00:00'),
(20, 'Empe Rh''s WS', '', '2015-08-25 00:00:00'),
(21, 'John Brylle''s WS', '', '2015-08-07 00:00:00'),
(22, 'Onetwotest''s WS', 'A workspace for intuitive learning experience centered on arhitecture.', '0000-00-00 00:00:00'),
(23, 'Huehuehue''s WS', '', '0000-00-00 00:00:00'),
(24, 'Spec''s WS', '', '0000-00-00 00:00:00'),
(25, 'Wtf''s WS', '', '0000-00-00 00:00:00'),
(26, 'Tyrion Lannister''s WS', '', '2015-09-06 20:05:57'),
(44, 'New Workspace', 'My new workz', '2015-09-09 03:06:45'),
(45, 'Wush''s WS', '', '2015-09-12 15:01:58'),
(46, 'Wuxxy''s WS', '', '2015-09-12 15:14:54'),
(47, 'Euz''s WS', '', '2015-09-12 15:16:17'),
(48, 'Hushy''s WS', '', '2015-09-12 17:59:24'),
(49, 'Wubeezy', 'Uuu', '2015-09-12 21:30:36'),
(50, 'My workspace', '', '2015-09-18 22:05:46'),
(51, 'MY WORKSPACEEEEEEEE', 'ORYTTTTTTTTTTTTT', '0000-00-00 00:00:00'),
(52, 'Ormoc Sti', '', '2015-09-23 09:13:58'),
(53, 'Silky smooth''s WS', '', '2015-09-23 14:41:46'),
(55, 'Ormoc Chamber', '', '0000-00-00 00:00:00'),
(56, 'Huyu''s WS', '', '2015-09-25 08:16:57'),
(57, 'Testtt', '', '2015-09-25 20:07:35'),
(58, 'Xiao''s WS', '', '2015-09-25 20:13:12'),
(59, 'wendell''s workspace', '', '0000-00-00 00:00:00'),
(60, 'nucleusOne''s workspace', '', '0000-00-00 00:00:00'),
(61, 'NucleusTwo''s WS', '', '2015-09-26 07:09:47'),
(62, 'wendellx''s workspace', '', '0000-00-00 00:00:00'),
(63, 'makudex''s workspace', '', '0000-00-00 00:00:00'),
(64, 'Thesis Final project', 'wetwew', '0000-00-00 00:00:00'),
(65, 'jovanne''s workspace', '', '0000-00-00 00:00:00'),
(66, 'Jhuko Junco''s WS', '', '2015-09-26 07:54:03'),
(67, 'Bug', '', '2015-09-26 17:43:19'),
(68, 'Jose Rizal''s workspace', '', '0000-00-00 00:00:00'),
(69, 'Jose Rizal''s workspace', '', '0000-00-00 00:00:00'),
(70, 'Gabriel Silang''s WS', '', '2015-09-26 18:56:51'),
(71, 'dsf', 'dsf', '0000-00-00 00:00:00'),
(72, 'wew''s workspace', '', '0000-00-00 00:00:00'),
(73, 'x', 'x', '0000-00-00 00:00:00'),
(74, 'sdf', 'sdf', '0000-00-00 00:00:00'),
(75, 'wendellx''s workspace', '', '0000-00-00 00:00:00'),
(76, 'Bagong workspace', 'bagong description', '0000-00-00 00:00:00'),
(77, 'new workspace', '', '0000-00-00 00:00:00'),
(79, 'BBB', '', '0000-00-00 00:00:00'),
(80, 'Mimi''s WS', '', '2015-09-27 13:27:14'),
(81, 'U', '', '2015-10-11 11:40:08'),
(82, 'Last one''s WS', '', '2015-10-11 11:48:03'),
(83, 'Last two''s WS', '', '2015-10-11 11:52:43'),
(84, 'Skwibird''s WS', '', '2015-10-14 20:31:53'),
(85, 'bagong user''s workspaces', '', '0000-00-00 00:00:00'),
(86, 'Another user''s workspace', '', '0000-00-00 00:00:00'),
(87, 'wendell madjus''s workspace', '', '0000-00-00 00:00:00'),
(88, 'brylle orito''s workspace', '', '0000-00-00 00:00:00'),
(89, 'Bagong workspace', 'new', '0000-00-00 00:00:00');

-- --------------------------------------------------------

--
-- Table structure for table `ws_invites`
--

CREATE TABLE IF NOT EXISTS `ws_invites` (
  `invite_id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `workspace_id` int(11) NOT NULL,
  `project_id` int(11) NOT NULL,
  `type` varchar(200) COLLATE utf8_unicode_ci NOT NULL,
  `email` varchar(200) COLLATE utf8_unicode_ci NOT NULL,
  `date_invited` datetime NOT NULL,
  PRIMARY KEY (`invite_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci AUTO_INCREMENT=185 ;

--
-- Dumping data for table `ws_invites`
--

INSERT INTO `ws_invites` (`invite_id`, `user_id`, `workspace_id`, `project_id`, `type`, `email`, `date_invited`) VALUES
(130, 124, 22, 19, 'Project', 'wew@mailinator.com', '2015-09-04 20:33:40'),
(132, 126, 24, 0, 'Workspace', 'eft@mailinator.com', '2015-09-04 20:41:17'),
(133, 126, 24, 0, 'Workspace', 'eftx@mailinator.com', '2015-09-04 21:06:09'),
(152, 29, 42, 0, 'Workspace', 'am@mailinator.com', '2015-09-08 02:13:03'),
(155, 32, 51, 0, 'Workspace', 'ormoc@mailinator.com', '2015-09-23 07:20:53'),
(161, 31, 55, 0, 'Workspace', 'celngo@yahoo.com', '2015-09-23 16:48:25'),
(168, 141, 64, 48, 'Project', 'nucleustwo@mailinator.com', '2015-09-26 07:55:10'),
(171, 142, 65, 50, 'Project', 'wendellx@mailinator.com', '2015-09-26 17:07:01'),
(172, 142, 65, 50, 'Project', 'why@mailinator.com', '2015-09-26 17:10:03'),
(173, 141, 64, 48, 'Project', 'joinme@mailinator.com', '2015-09-26 17:33:38'),
(183, 151, 83, 61, 'Project', 'bagongemail@mailinator.com', '2015-10-14 20:30:50'),
(184, 153, 85, 62, 'Project', 'wmadjus@mailinator.com', '2015-10-17 14:27:52');

-- --------------------------------------------------------

--
-- Table structure for table `ws_members`
--

CREATE TABLE IF NOT EXISTS `ws_members` (
  `workspace_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `user_level_id` int(11) NOT NULL,
  `date_joined` datetime NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Dumping data for table `ws_members`
--

INSERT INTO `ws_members` (`workspace_id`, `user_id`, `user_level_id`, `date_joined`) VALUES
(17, 29, 2, '0000-00-00 00:00:00'),
(18, 30, 1, '0000-00-00 00:00:00'),
(19, 31, 1, '0000-00-00 00:00:00'),
(20, 32, 3, '0000-00-00 00:00:00'),
(21, 33, 1, '0000-00-00 00:00:00'),
(22, 124, 1, '0000-00-00 00:00:00'),
(17, 31, 1, '0000-00-00 00:00:00'),
(21, 31, 3, '0000-00-00 00:00:00'),
(23, 125, 2, '0000-00-00 00:00:00'),
(19, 125, 2, '0000-00-00 00:00:00'),
(24, 126, 1, '0000-00-00 00:00:00'),
(25, 127, 1, '0000-00-00 00:00:00'),
(25, 125, 2, '0000-00-00 00:00:00'),
(17, 125, 2, '0000-00-00 00:00:00'),
(17, 32, 3, '0000-00-00 00:00:00'),
(20, 31, 3, '0000-00-00 00:00:00'),
(26, 128, 1, '0000-00-00 00:00:00'),
(23, 29, 2, '0000-00-00 00:00:00'),
(44, 29, 2, '0000-00-00 00:00:00'),
(45, 129, 1, '0000-00-00 00:00:00'),
(46, 130, 1, '0000-00-00 00:00:00'),
(47, 131, 1, '0000-00-00 00:00:00'),
(23, 129, 3, '0000-00-00 00:00:00'),
(48, 132, 1, '0000-00-00 00:00:00'),
(49, 133, 2, '0000-00-00 00:00:00'),
(49, 31, 3, '0000-00-00 00:00:00'),
(49, 29, 2, '0000-00-00 00:00:00'),
(50, 29, 2, '0000-00-00 00:00:00'),
(17, 133, 2, '0000-00-00 00:00:00'),
(51, 32, 1, '2015-09-23 07:20:53'),
(52, 133, 1, '2015-09-23 09:13:58'),
(53, 134, 1, '2015-09-23 14:41:46'),
(55, 31, 1, '2015-09-23 16:48:25'),
(55, 29, 2, '2015-09-23 16:55:51'),
(56, 135, 1, '2015-09-25 08:16:57'),
(17, 135, 3, '2015-09-25 08:17:52'),
(56, 130, 3, '2015-09-25 09:15:25'),
(55, 32, 3, '2015-09-25 18:08:25'),
(57, 32, 1, '2015-09-25 20:07:35'),
(58, 136, 2, '2015-09-25 20:13:12'),
(57, 136, 2, '2015-09-25 20:13:32'),
(59, 137, 1, '2015-09-26 07:09:19'),
(60, 138, 3, '2015-09-26 07:09:21'),
(61, 139, 1, '2015-09-26 07:09:47'),
(62, 140, 1, '2015-09-26 07:29:29'),
(63, 141, 1, '2015-09-26 07:42:13'),
(64, 141, 1, '2015-09-26 07:43:13'),
(65, 142, 1, '2015-09-26 07:53:47'),
(66, 143, 1, '2015-09-26 07:54:03'),
(64, 143, 3, '2015-09-26 07:54:32'),
(64, 142, 3, '2015-09-26 07:56:16'),
(64, 138, 3, '2015-09-26 07:56:42'),
(67, 133, 1, '2015-09-26 17:43:19'),
(55, 133, 3, '2015-09-26 17:55:04'),
(68, 144, 1, '2015-09-26 18:36:13'),
(69, 145, 1, '2015-09-26 18:37:44'),
(70, 146, 1, '2015-09-26 18:56:51'),
(70, 145, 3, '2015-09-26 19:07:23'),
(71, 142, 1, '2015-09-27 07:51:35'),
(72, 147, 1, '2015-09-27 07:52:52'),
(73, 147, 1, '2015-09-27 07:56:08'),
(74, 147, 1, '2015-09-27 07:57:45'),
(75, 148, 1, '2015-09-27 10:40:32'),
(76, 148, 1, '2015-09-27 10:41:13'),
(77, 142, 1, '2015-09-27 10:51:21'),
(77, 32, 3, '2015-09-27 10:52:08'),
(79, 145, 1, '2015-09-27 11:12:39'),
(80, 149, 1, '2015-09-27 13:27:14'),
(81, 29, 1, '2015-10-11 11:40:08'),
(82, 150, 1, '2015-10-11 11:48:03'),
(81, 150, 2, '2015-10-11 11:48:27'),
(83, 151, 1, '2015-10-11 11:52:43'),
(81, 151, 3, '2015-10-11 11:53:17'),
(83, 29, 3, '2015-10-12 22:08:33'),
(84, 152, 1, '2015-10-14 20:31:53'),
(83, 152, 3, '2015-10-14 20:32:19'),
(85, 153, 1, '2015-10-17 14:26:12'),
(86, 154, 1, '2015-10-17 14:33:32'),
(85, 154, 3, '2015-10-17 14:33:43'),
(87, 155, 1, '2015-10-17 16:28:55'),
(88, 156, 1, '2015-10-17 16:29:54'),
(89, 155, 1, '2015-10-17 16:30:06'),
(89, 156, 3, '2015-10-17 16:30:22');

--
-- Constraints for dumped tables
--

--
-- Constraints for table `tbl_usersinfo`
--
ALTER TABLE `tbl_usersinfo`
  ADD CONSTRAINT `tbl_usersinfo_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `tbl_users` (`user_id`) ON DELETE CASCADE;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
